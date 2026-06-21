package net.kuina.magitech.relic;

public record PlacedRelic(
    RelicData data,
    int posX,
    int posY,
    int rotation
) {
    public RelicShape getRotatedShape() {
        return data.shape().rotateTimes(rotation);
    }

    public boolean occupiesCell(int x, int y) {
        RelicShape rotated = getRotatedShape();
        int localX = x - posX;
        int localY = y - posY;
        return rotated.get(localX, localY);
    }
}
