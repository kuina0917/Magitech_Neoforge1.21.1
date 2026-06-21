package net.kuina.magitech.relic;

import java.util.BitSet;

public record RelicShape(int width, int height, BitSet cells) {

    public static RelicShape single() {
        BitSet bits = new BitSet(1);
        bits.set(0);
        return new RelicShape(1, 1, bits);
    }

    public static RelicShape horizontal(int len) {
        BitSet bits = new BitSet(len);
        bits.set(0, len);
        return new RelicShape(len, 1, bits);
    }

    public static RelicShape vertical(int len) {
        BitSet bits = new BitSet(len);
        bits.set(0, len);
        return new RelicShape(1, len, bits);
    }

    public static RelicShape square(int size) {
        BitSet bits = new BitSet(size * size);
        bits.set(0, size * size);
        return new RelicShape(size, size, bits);
    }

    public static RelicShape LShape() {
        BitSet bits = new BitSet(4);
        bits.set(0);
        bits.set(1);
        bits.set(2);
        return new RelicShape(2, 2, bits);
    }

    public static RelicShape TShape() {
        BitSet bits = new BitSet(6);
        bits.set(0); bits.set(1); bits.set(2);
        bits.set(4);
        return new RelicShape(3, 2, bits);
    }

    public static RelicShape cross() {
        BitSet bits = new BitSet(9);
        bits.set(1);
        bits.set(3); bits.set(4); bits.set(5);
        bits.set(7);
        return new RelicShape(3, 3, bits);
    }

    public boolean get(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return false;
        return cells.get(y * width + x);
    }

    public RelicShape rotate() {
        BitSet rotated = new BitSet(width * height);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cells.get(y * width + x)) {
                    rotated.set(x * height + (height - 1 - y));
                }
            }
        }
        return new RelicShape(height, width, rotated);
    }

    public RelicShape rotateTimes(int times) {
        RelicShape shape = this;
        for (int i = 0; i < (times & 3); i++) {
            shape = shape.rotate();
        }
        return shape;
    }

    public int getCellCount() {
        return cells.cardinality();
    }

    public byte[] toByteArray() {
        int size = width * height;
        byte[] arr = new byte[size];
        for (int i = 0; i < size; i++) {
            arr[i] = (byte) (cells.get(i) ? 1 : 0);
        }
        return arr;
    }

    public static RelicShape fromByteArray(int w, int h, byte[] arr) {
        BitSet bits = new BitSet(w * h);
        for (int i = 0; i < arr.length && i < w * h; i++) {
            if (arr[i] != 0) bits.set(i);
        }
        return new RelicShape(w, h, bits);
    }
}
