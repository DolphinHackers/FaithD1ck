package sb.faithd1ck.hackerdetector.data;

public class SampleListI {

    private final int[] data;
    /** The maximum size */
    private final int capacity;
    /** The current size of the list */
    private int size;
    /** The array index of the last element inserted */
    private int latestIndex;

    public SampleListI(int capacity) {
        if (capacity < 2) {
            throw new IllegalArgumentException("Size must be at least 2");
        }
        this.data = new int[capacity];
        this.capacity = capacity;
        this.size = 0;
        this.latestIndex = -1;
    }

    public void add(int e) {
        this.latestIndex = (this.latestIndex + 1) % this.capacity;
        this.data[this.latestIndex] = e;
        if (this.size < this.capacity) this.size++;
    }

    /**
     * get(0) will return the latest element insert,
     * get(capacity - 1) will return the oldest element
     */
    public int get(int index) {
        if (index < 0 || index > this.size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final int i = this.latestIndex - index;
        return this.data[i < 0 ? i + this.capacity : i];
    }

    public void clear() {
        this.size = 0;
        this.latestIndex = -1;
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.capacity;
    }

    public boolean hasCollected() {
        return size == capacity;
    }

    public int sum() {
        int s = 0;
        for (int i = 0; i < this.size; i++) {
            s += this.data[i];
        }
        return s;
    }

    public float average() {
        return this.sum() / (float) this.size;
    }

    public boolean isSameValues() {
        if (this.size < 2) return false;
        final int v = this.get(0);
        for (int i = 1; i < this.size; i++) {
            if (v != this.get(i)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        if (this.size == 0) {
            return "[]";
        }
        final StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(this.get(i));
            if (i == this.size - 1) {
                return b.append(']').toString();
            }
            b.append(", ");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final SampleListI list = (SampleListI) other;
        if (size != list.size) return false;
        if (size == 0) return true;
        for (int i = 0; ; i++) {
            if (this.get(i) != list.get(i)) return false;
            if (i == this.size - 1) return true;
        }
    }

}
