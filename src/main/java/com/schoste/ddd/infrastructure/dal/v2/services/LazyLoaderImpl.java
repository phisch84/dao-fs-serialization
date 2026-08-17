package com.schoste.ddd.infrastructure.dal.v2.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.schoste.ddd.infrastructure.dal.v2.models.GenericDataObject;

/**
 * Implementation of the {@link LazyLoader} interface for file-system and serialization DAOs
 */
public class LazyLoaderImpl extends GenericLazyLoader<Integer, GenericDataObject>
{
    protected Stream<Path> storagePathStream;
    protected Iterator<Path> storagePathIterator;
    
    public LazyLoaderImpl(Function<Integer, GenericDataObject> fileToDataObjFunc, Path storagePath) throws Exception
    {
        super(fileToDataObjFunc);

        if (storagePath == null) throw new IllegalArgumentException();

        this.storagePathStream = Files.walk(storagePath);
        this.storagePathIterator = this.storagePathStream.iterator();
    }

    @Override
    public boolean tryAdvance(Consumer<? super GenericDataObject> action) 
    {
        if (!this.storagePathIterator.hasNext()) return false;

        Path nextFile = this.storagePathIterator.next();

        if (!Files.isRegularFile(nextFile)) return true;
        
        String fileName = nextFile.getFileName().toString();
        int id = Integer.valueOf(fileName);
        GenericDataObject nextDataObj = super.sourceRecordToDataObjConversionFn.apply(id);

        action.accept(nextDataObj);

        return true;
    }

    @Override
    public Spliterator<GenericDataObject> trySplit() 
    {
        return null;
    }

    @Override
    public long estimateSize() 
    {
        return this.storagePathStream.count();
    }

    @Override
    public int characteristics() 
    {
        return Spliterator.IMMUTABLE;
    }

    @Override
    public void close() 
    {
        if (this.storagePathStream != null) this.storagePathStream.close();

        this.storagePathIterator = null;
    }
}