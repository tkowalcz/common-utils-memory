package pl.codewise.util.buffer;

import org.assertj.core.api.AbstractAssert;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractByteBufferAssert extends AbstractAssert<AbstractByteBufferAssert, AbstractByteBuffer<?>> {

    AbstractByteBufferAssert(AbstractByteBuffer<?> actual) {
        super(actual, AbstractByteBufferAssert.class);
    }

    protected void failIsNot(String propertyName, Object expected, Object actual) {
        failWithMessage("Expected %s to be <%s> but was <%s>", propertyName, expected, actual);
    }

    protected void failIs(String propertyName, Object expected) {
        failWithMessage("Expected %s to be other than <%s>", expected);
    }

    public AbstractByteBufferAssert initializedIsEqualTo(boolean expected) {
        isNotNull();
        if (actual.initialized != expected) {
            failIsNot("initialized", expected, actual.initialized);
        }
        return myself;
    }

    public AbstractByteBufferAssert isInitialized() {
        return initializedIsEqualTo(true);
    }

    public AbstractByteBufferAssert isNotInitialzied() {
        return initializedIsEqualTo(false);
    }

    public AbstractByteBufferAssert memoryIsNull() {
        isNotNull();
        if (actual.memory != null) {
            failIsNot("memory", "not null", "null");
        }
        return myself;
    }

    public AbstractByteBufferAssert memoryIsNotNull() {
        isNotNull();
        if (actual.memory == null) {
            failIsNot("memory", "null", "not null");
        }
        return myself;
    }

    public AbstractByteBufferAssert memoryIsSameAs(ByteBufferMemory expected) {
        if (actual.memory != expected) {
            failIsNot("memory", expected, actual.memory);
        }
        isInitialized();
        return this;
    }

    public AbstractByteBufferAssert baseOffsetIsEqualTo(int expected) {
        isNotNull();
        if (actual.baseOffset != expected) {
            failIsNot("baseOffset", expected, actual.baseOffset);
        }
        return myself;
    }

    public AbstractByteBufferAssert baseOffsetIsNotEqualTo(int other) {
        isInitialized();
        if (actual.baseOffset == other) {
            failIs("baseOffset", other);
        }
        return myself;
    }

    public AbstractByteBufferAssert capacityIsEqualTo(int expected) {
        isInitialized();
        if (actual.capacity() != expected) {
            failIsNot("capacity", expected, actual.capacity());
        }
        return myself;
    }

    public AbstractByteBufferAssert positionIsEqualTo(int expected) {
        isInitialized();
        if (actual.position != expected) {
            failIsNot("position", expected, actual.position());
        }
        return myself;
    }

    public AbstractByteBufferAssert positionFieldIsEqualTo(int expected) {
        isNotNull();
        if (actual.position != expected) {
            failIsNot("postion field", expected, actual.position);
        }
        return myself;
    }

    public AbstractByteBufferAssert limitIsEqualTo(int expected) {
        isInitialized();
        if (actual.limit() != expected) {
            failIsNot("limit", expected, actual.limit());
        }
        return myself;
    }

    public AbstractByteBufferAssert limitIsAtCapacity() {
        isNotNull();
        if (actual.limit != AbstractByteBuffer.LIMIT_AT_CAPACITY) {
            failIsNot("limit field", "LIMIT_AT_CAPACITY", actual.limit);
        }
        return myself;
    }

    public AbstractByteBufferAssert limitFieldIsEqualTo(int expected) {
        isNotNull();
        if (actual.limit != expected) {
            failIsNot("limit field", expected, actual.limit);
        }
        return myself;
    }

    public void containsExactly(byte... expectedBytes) {
        isNotNull();
        assertThat(actual).isEqualTo(Buffers.wrap(expectedBytes));
    }
}
