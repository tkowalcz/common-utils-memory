package com.codewise.util.buffer;

import com.codewise.util.memory.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Function;

@BenchmarkMode(Mode.Throughput)
public class ByteBufferMemoryMicroBenchmark {

    public static final int MEMORY_SIZE = 1024;

    public enum MemoryType {
        NIO, FIXED, GROWABLE, PAGED
    }

    public enum AccessType {
        SAFE, UNSAFE
    }

    @State(Scope.Thread)
    public static class GenericMemoryBenchmarkState {

        @Param({"NIO", "FIXED", "GROWABLE", "PAGED"})
        public MemoryType memoryType;
        @Param({"SAFE", "UNSAFE"})
        public AccessType accessType;

        public BiConsumer<Integer, Byte> putByte;
        public BiConsumer<Integer, Integer> putInt;
        public BiConsumer<Integer, Long> putLong;
        public Function<Integer, Byte> getByte;
        public Function<Integer, Integer> getInt;
        public Function<Integer, Long> getLong;

        public int index = 17;

        @Setup(Level.Trial)
        public void setUpMemory() {
            switch (memoryType) {
                case NIO:
                    ByteBuffer bufNio = accessType == AccessType.SAFE ?
                            ByteBuffer.allocate(MEMORY_SIZE * 2) :
                            ByteBuffer.allocateDirect(MEMORY_SIZE * 2);
                    putByte = bufNio::put;
                    putInt = bufNio::putInt;
                    putLong = bufNio::putLong;
                    getByte = bufNio::get;
                    getInt = bufNio::getInt;
                    getLong = bufNio::getLong;
                    break;
                case FIXED:
                    MutableMemory bufFixed = accessType == AccessType.SAFE ?
                            new FixedSafeMutableMemory(MEMORY_SIZE * 2) {
                            } :
                            new FixedUnsafeMutableMemory(MEMORY_SIZE * 2) {
                            };
                    putByte = bufFixed::put;
                    putInt = bufFixed::putInt;
                    putLong = bufFixed::putLong;
                    getByte = bufFixed::get;
                    getInt = bufFixed::getInt;
                    getLong = bufFixed::getLong;
                    break;
                case GROWABLE:
                    MutableMemory bufGrowable = accessType == AccessType.SAFE ?
                            new GrowableSafeMutableMemory(MEMORY_SIZE * 2) {
                            } :
                            new GrowableUnsafeMutableMemory(MEMORY_SIZE * 2) {
                            };
                    putByte = bufGrowable::put;
                    putInt = bufGrowable::putInt;
                    putLong = bufGrowable::putLong;
                    getByte = bufGrowable::get;
                    getInt = bufGrowable::getInt;
                    getLong = bufGrowable::getLong;
                    break;
                case PAGED:
                    MutableMemory bufPaged = accessType == AccessType.SAFE ?
                            new PagedSafeMutableMemory(4, Integer.numberOfTrailingZeros(MEMORY_SIZE), MEMORY_SIZE * 2) {
                            } :
                            new PagedUnsafeMutableMemory(4, Integer.numberOfTrailingZeros(MEMORY_SIZE), MEMORY_SIZE * 2) {
                            };
                    putByte = bufPaged::put;
                    putInt = bufPaged::putInt;
                    putLong = bufPaged::putLong;
                    getByte = bufPaged::get;
                    getInt = bufPaged::getInt;
                    getLong = bufPaged::getLong;
                    break;
            }
        }

        @Setup(Level.Invocation)
        public void nextIndex() {
            index = (31 + index) & (MEMORY_SIZE - 1);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void genericMemoryBenchmark(GenericMemoryBenchmarkState state, Blackhole blackhole) {
        BiConsumer<Integer, Byte> putByte = state.putByte;
        BiConsumer<Integer, Integer> putInt = state.putInt;
        BiConsumer<Integer, Long> putLong = state.putLong;
        Function<Integer, Byte> getByte = state.getByte;
        Function<Integer, Integer> getInt = state.getInt;
        Function<Integer, Long> getLong = state.getLong;
        int pos = state.index;
        for (int idx = 0; idx < 16; idx++) {
            putByte.accept(pos++, (byte) 1);
        }
        for (int idx = 0; idx < 4; idx++) {
            putInt.accept(pos, 1);
            pos += 4;
        }
        for (int idx = 0; idx < 2; idx++) {
            putLong.accept(pos, 1l);
            pos += 8;
        }
        for (int idx = 0; idx < 16; idx++) {
            blackhole.consume(getByte.apply(pos--));
        }
        for (int idx = 0; idx < 2; idx++) {
            blackhole.consume(getLong.apply(pos));
            pos -= 8;
        }
        for (int idx = 0; idx < 4; idx++) {
            blackhole.consume(getInt.apply(pos));
            pos -= 4;
        }
    }

    @State(Scope.Thread)
    public static class ByteBufferMemoryBenchmarkState {

        @Param({"FIXED", "GROWABLE", "PAGED"})
//        @Param({"PAGED"})
        public MemoryType memoryType;
        @Param({"SAFE", "UNSAFE"})
//        @Param({"UNSAFE"})
        public AccessType accessType;

        public MutableMemory memory;
        public int index = 17;

        @Setup(Level.Trial)
        public void setUpMemory() {
            switch (memoryType) {
                case FIXED:
                    memory = accessType == AccessType.SAFE ?
                            new FixedSafeMutableMemory(MEMORY_SIZE * 2) {
                            } :
                            new FixedUnsafeMutableMemory(MEMORY_SIZE * 2) {
                            };
                    break;
                case GROWABLE:
                    memory = accessType == AccessType.SAFE ?
                            new GrowableSafeMutableMemory(MEMORY_SIZE * 2) {
                            } :
                            new GrowableUnsafeMutableMemory(MEMORY_SIZE * 2) {
                            };
                    break;
                case PAGED:
                    memory = accessType == AccessType.SAFE ?
                            new PagedSafeMutableMemory(4, Integer.numberOfTrailingZeros(MEMORY_SIZE), MEMORY_SIZE * 2) {
                            } :
                            new PagedUnsafeMutableMemory(4, Integer.numberOfTrailingZeros(MEMORY_SIZE), MEMORY_SIZE * 2) {
                            };
                    break;
            }
        }

        @Setup(Level.Invocation)
        public void nextIndex() {
            index = (31 + index) & (MEMORY_SIZE - 1);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
//    @CompilerControl(CompilerControl.Mode.PRINT)
    public void byteBufferMemoryBenchmark(ByteBufferMemoryBenchmarkState state, Blackhole blackhole) {
        MutableMemory memory = state.memory;
        int pos = state.index;
//        memory.putInt(pos, 1);
        for (int idx = 0; idx < 16; idx++) {
            memory.put(pos++, (byte) 1);
        }
        for (int idx = 0; idx < 4; idx++) {
            memory.putInt(pos, 1);
            pos += 4;
        }
        for (int idx = 0; idx < 2; idx++) {
            memory.putLong(pos, 1l);
            pos += 8;
        }
        for (int idx = 0; idx < 16; idx++) {
            blackhole.consume(memory.get(pos--));
        }
        for (int idx = 0; idx < 2; idx++) {
            blackhole.consume(memory.getLong(pos));
            pos -= 8;
        }
        for (int idx = 0; idx < 4; idx++) {
            blackhole.consume(memory.getInt(pos));
            pos -= 4;
        }
    }

/*
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + ByteBufferMemoryMicroBenchmark.class.getSimpleName() + ".*Benchmark")
                .warmupIterations(10)
                .measurementIterations(15)
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
*/
}
