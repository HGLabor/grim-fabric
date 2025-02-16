package org.bukkit.util;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;
import java.util.Map;
import java.util.Random;

//@SerializableAs("Vector")
public class Vector implements Cloneable {
    private static Random random = new Random();
    private static final double epsilon = 1.0E-6;
    protected double x;
    protected double y;
    protected double z;

    public Vector() {
        this.x = (double) 0.0F;
        this.y = (double) 0.0F;
        this.z = (double) 0.0F;
    }

    public Vector(int x, int y, int z) {
        this.x = (double) x;
        this.y = (double) y;
        this.z = (double) z;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z) {
        this.x = (double) x;
        this.y = (double) y;
        this.z = (double) z;
    }

    public @NotNull Vector add(@NotNull Vector vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    public @NotNull Vector subtract(@NotNull Vector vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        this.z -= vec.z;
        return this;
    }

    public @NotNull Vector multiply(@NotNull Vector vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        this.z *= vec.z;
        return this;
    }

    public @NotNull Vector divide(@NotNull Vector vec) {
        this.x /= vec.x;
        this.y /= vec.y;
        this.z /= vec.z;
        return this;
    }

    public @NotNull Vector copy(@NotNull Vector vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
        return this;
    }

    public double length() {
        return Math.sqrt(NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z));
    }

    public double lengthSquared() {
        return NumberConversions.square(this.x) + NumberConversions.square(this.y) + NumberConversions.square(this.z);
    }

    public double distance(@NotNull Vector o) {
        return Math.sqrt(NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z));
    }

    public double distanceSquared(@NotNull Vector o) {
        return NumberConversions.square(this.x - o.x) + NumberConversions.square(this.y - o.y) + NumberConversions.square(this.z - o.z);
    }

    public float angle(@NotNull Vector other) {
        double dot = Doubles.constrainToRange(this.dot(other) / (this.length() * other.length()), (double) -1.0F, (double) 1.0F);
        return (float) Math.acos(dot);
    }

    public @NotNull Vector midpoint(@NotNull Vector other) {
        this.x = (this.x + other.x) / (double) 2.0F;
        this.y = (this.y + other.y) / (double) 2.0F;
        this.z = (this.z + other.z) / (double) 2.0F;
        return this;
    }

    public @NotNull Vector getMidpoint(@NotNull Vector other) {
        double x = (this.x + other.x) / (double) 2.0F;
        double y = (this.y + other.y) / (double) 2.0F;
        double z = (this.z + other.z) / (double) 2.0F;
        return new Vector(x, y, z);
    }

    public @NotNull Vector multiply(int m) {
        this.x *= (double) m;
        this.y *= (double) m;
        this.z *= (double) m;
        return this;
    }

    public @NotNull Vector multiply(double m) {
        this.x *= m;
        this.y *= m;
        this.z *= m;
        return this;
    }

    public @NotNull Vector multiply(float m) {
        this.x *= (double) m;
        this.y *= (double) m;
        this.z *= (double) m;
        return this;
    }

    public double dot(@NotNull Vector other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }

    public @NotNull Vector crossProduct(@NotNull Vector o) {
        double newX = this.y * o.z - o.y * this.z;
        double newY = this.z * o.x - o.z * this.x;
        double newZ = this.x * o.y - o.x * this.y;
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        return this;
    }

    public @NotNull Vector getCrossProduct(@NotNull Vector o) {
        double x = this.y * o.z - o.y * this.z;
        double y = this.z * o.x - o.z * this.x;
        double z = this.x * o.y - o.x * this.y;
        return new Vector(x, y, z);
    }

    public @NotNull Vector normalize() {
        double length = this.length();
        this.x /= length;
        this.y /= length;
        this.z /= length;
        return this;
    }

    public @NotNull Vector zero() {
        this.x = (double) 0.0F;
        this.y = (double) 0.0F;
        this.z = (double) 0.0F;
        return this;
    }

    public boolean isZero() {
        return this.x == (double) 0.0F && this.y == (double) 0.0F && this.z == (double) 0.0F;
    }

    @NotNull Vector normalizeZeros() {
        if (this.x == (double) -0.0F) {
            this.x = (double) 0.0F;
        }

        if (this.y == (double) -0.0F) {
            this.y = (double) 0.0F;
        }

        if (this.z == (double) -0.0F) {
            this.z = (double) 0.0F;
        }

        return this;
    }

    public boolean isInAABB(@NotNull Vector min, @NotNull Vector max) {
        return this.x >= min.x && this.x <= max.x && this.y >= min.y && this.y <= max.y && this.z >= min.z && this.z <= max.z;
    }

    public boolean isInSphere(@NotNull Vector origin, double radius) {
        return NumberConversions.square(origin.x - this.x) + NumberConversions.square(origin.y - this.y) + NumberConversions.square(origin.z - this.z) <= NumberConversions.square(radius);
    }

    public boolean isNormalized() {
        return Math.abs(this.lengthSquared() - (double) 1.0F) < getEpsilon();
    }

    public @NotNull Vector rotateAroundX(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double y = angleCos * this.getY() - angleSin * this.getZ();
        double z = angleSin * this.getY() + angleCos * this.getZ();
        return this.setY(y).setZ(z);
    }

    public @NotNull Vector rotateAroundY(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double x = angleCos * this.getX() + angleSin * this.getZ();
        double z = -angleSin * this.getX() + angleCos * this.getZ();
        return this.setX(x).setZ(z);
    }

    public @NotNull Vector rotateAroundZ(double angle) {
        double angleCos = Math.cos(angle);
        double angleSin = Math.sin(angle);
        double x = angleCos * this.getX() - angleSin * this.getY();
        double y = angleSin * this.getX() + angleCos * this.getY();
        return this.setX(x).setY(y);
    }

    public @NotNull Vector rotateAroundAxis(@NotNull Vector axis, double angle) throws IllegalArgumentException {
        Preconditions.checkArgument(axis != null, "The provided axis vector was null");
        return this.rotateAroundNonUnitAxis(axis.isNormalized() ? axis : axis.clone().normalize(), angle);
    }

    public @NotNull Vector rotateAroundNonUnitAxis(@NotNull Vector axis, double angle) throws IllegalArgumentException {
        Preconditions.checkArgument(axis != null, "The provided axis vector was null");
        double x = this.getX();
        double y = this.getY();
        double z = this.getZ();
        double x2 = axis.getX();
        double y2 = axis.getY();
        double z2 = axis.getZ();
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        double dotProduct = this.dot(axis);
        double xPrime = x2 * dotProduct * ((double) 1.0F - cosTheta) + x * cosTheta + (-z2 * y + y2 * z) * sinTheta;
        double yPrime = y2 * dotProduct * ((double) 1.0F - cosTheta) + y * cosTheta + (z2 * x - x2 * z) * sinTheta;
        double zPrime = z2 * dotProduct * ((double) 1.0F - cosTheta) + z * cosTheta + (-y2 * x + x2 * y) * sinTheta;
        return this.setX(xPrime).setY(yPrime).setZ(zPrime);
    }

    public double getX() {
        return this.x;
    }

    public int getBlockX() {
        return NumberConversions.floor(this.x);
    }

    public double getY() {
        return this.y;
    }

    public int getBlockY() {
        return NumberConversions.floor(this.y);
    }

    public double getZ() {
        return this.z;
    }

    public int getBlockZ() {
        return NumberConversions.floor(this.z);
    }

    public @NotNull Vector setX(int x) {
        this.x = (double) x;
        return this;
    }

    public @NotNull Vector setX(double x) {
        this.x = x;
        return this;
    }

    public @NotNull Vector setX(float x) {
        this.x = (double) x;
        return this;
    }

    public @NotNull Vector setY(int y) {
        this.y = (double) y;
        return this;
    }

    public @NotNull Vector setY(double y) {
        this.y = y;
        return this;
    }

    public @NotNull Vector setY(float y) {
        this.y = (double) y;
        return this;
    }

    public @NotNull Vector setZ(int z) {
        this.z = (double) z;
        return this;
    }

    public @NotNull Vector setZ(double z) {
        this.z = z;
        return this;
    }

    public @NotNull Vector setZ(float z) {
        this.z = (double) z;
        return this;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector other)) {
            return false;
        } else {
            return Math.abs(this.x - other.x) < 1.0E-6 && Math.abs(this.y - other.y) < 1.0E-6 && Math.abs(this.z - other.z) < 1.0E-6 && this.getClass().equals(obj.getClass());
        }
    }

    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        return hash;
    }

    public @NotNull Vector clone() {
        try {
            return (Vector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public String toString() {
        return this.x + "," + this.y + "," + this.z;
    }

    /*public @NotNull Location toLocation(@NotNull World world) {
        return new Location(world, this.x, this.y, this.z);
    }

    public @NotNull Location toLocation(@NotNull World world, float yaw, float pitch) {
        return new Location(world, this.x, this.y, this.z, yaw, pitch);
    }

    public @NotNull BlockVector toBlockVector() {
        return new BlockVector(this.x, this.y, this.z);
    }*/

    public @NotNull Vector3f toVector3f() {
        return new Vector3f((float) this.x, (float) this.y, (float) this.z);
    }

    public @NotNull Vector3d toVector3d() {
        return new Vector3d(this.x, this.y, this.z);
    }

    public @NotNull Vector3i toVector3i(int roundingMode) {
        return new Vector3i(this.x, this.y, this.z, roundingMode);
    }

    public @NotNull Vector3i toVector3i() {
        return this.toVector3i(2);
    }

    public void checkFinite() throws IllegalArgumentException {
        NumberConversions.checkFinite(this.x, "x not finite");
        NumberConversions.checkFinite(this.y, "y not finite");
        NumberConversions.checkFinite(this.z, "z not finite");
    }

    public static double getEpsilon() {
        return 1.0E-6;
    }

    public static @NotNull Vector getMinimum(@NotNull Vector v1, @NotNull Vector v2) {
        return new Vector(Math.min(v1.x, v2.x), Math.min(v1.y, v2.y), Math.min(v1.z, v2.z));
    }

    public static @NotNull Vector getMaximum(@NotNull Vector v1, @NotNull Vector v2) {
        return new Vector(Math.max(v1.x, v2.x), Math.max(v1.y, v2.y), Math.max(v1.z, v2.z));
    }

    public static @NotNull Vector getRandom() {
        return new Vector(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }

    public static @NotNull Vector fromJOML(@NotNull Vector3f vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static @NotNull Vector fromJOML(@NotNull Vector3d vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static @NotNull Vector fromJOML(@NotNull Vector3i vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static @NotNull Vector fromJOML(@NotNull Vector3fc vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static @NotNull Vector fromJOML(@NotNull Vector3dc vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static @NotNull Vector fromJOML(@NotNull Vector3ic vector) {
        return new Vector(vector.x(), vector.y(), vector.z());
    }

    public static @NotNull Vector deserialize(@NotNull Map<String, Object> args) {
        double x = (double) 0.0F;
        double y = (double) 0.0F;
        double z = (double) 0.0F;
        if (args.containsKey("x")) {
            x = (Double) args.get("x");
        }

        if (args.containsKey("y")) {
            y = (Double) args.get("y");
        }

        if (args.containsKey("z")) {
            z = (Double) args.get("z");
        }

        return new Vector(x, y, z);
    }
}
