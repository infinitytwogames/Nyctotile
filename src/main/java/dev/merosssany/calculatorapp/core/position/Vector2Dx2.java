package dev.merosssany.calculatorapp.core.position;

public class Vector2Dx2<T extends Number & Comparable<T>> {
    private Vector2D<T> point1;
    private Vector2D<T> point2;

    public Vector2Dx2(Vector2D<T> point1, Vector2D<T> point2) {
        this.point1 = point1;
        this.point2 = point2;
    }

    public Vector2Dx2(T x1,T y1, T x2, T y2) {
        point1 = new Vector2D<>(x1,y1);
        point2 = new Vector2D<>(x2,y2);
    }

    public Vector2D<T> getPoint1() {
        return point1;
    }

    public void setPoint1(Vector2D<T> point1) {
        this.point1 = point1;
    }

    public Vector2D<T> getPoint2() {
        return point2;
    }

    public void setPoint2(Vector2D<T> point2) {
        this.point2 = point2;
    }
}
