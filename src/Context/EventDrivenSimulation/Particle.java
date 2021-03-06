package Context.EventDrivenSimulation;

import Fundamentals.utils.StdDraw;
import Fundamentals.utils.StdRandom;

import java.awt.*;

/**
 * 运动的粒子对象
 */
public class  Particle {
    private static final double INFINITY = Double.POSITIVE_INFINITY;
    
    //位置
    private double rx;
    private double ry;
    
    //速度
    private double vx;
    private double vy;
    
    //碰撞数
    private int count;
    
    private final double radius;
    
    private final double mass;
    
    private Color color;
    
    public Particle() {
        rx = StdRandom.uniform(0.0, 1.0);
        ry = StdRandom.uniform(0.0, 1.0);
        vx = StdRandom.uniform(-0.005, 0.005);
        vy = StdRandom.uniform(-0.005, 0.005);
        radius = 0.02;
        mass = 0.5;
        color = Color.BLACK;
    }
    
    public Particle(double rx, double ry, double vx, double vy, double radius, double mass, Color color) {
        this.rx = rx;
        this.ry = ry;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.mass = mass;
        this.color = color;
    }
    
    
    //根据时间算出移动坐标
    public void move(double dt) {
        rx += vx * dt;
        ry += vy * dt;
    }
    
    
    //利用gui画出粒子
    public void draw() {
        StdDraw.setPenColor(color);
        StdDraw.filledCircle(rx, ry, radius);
    }
    
    //当前粒子参与碰撞的总数
    public int count() {
        return count;
    }
    
    /**
     * 当前粒子和that粒子碰撞所需时间
     *
     * @param that 另外一个粒子
     * @return 时间
     */
    public double timeToHit(Particle that) {
        if (this == that) return INFINITY;
        double dx = that.rx - this.rx;
        double dy = that.ry - this.ry;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double dvdr = dx * dvx + dy * dvy;
        if (dvdr > 0) return INFINITY;
        double dvdv = dvx * dvx + dvy * dvy;
        if (dvdv == 0) return INFINITY;
        double drdr = dx * dx + dy * dy;
        double sigma = this.radius + that.radius;
        double d = (dvdr * dvdr) - dvdv * (drdr - sigma * sigma);
        if (d < 0) return INFINITY;
        return -(dvdr + Math.sqrt(d)) / dvdv;
    }
    
    /**
     * 撞到垂直墙体所需时间
     *
     * @return 时间
     */
    public double timeToHitVerticalWall() {
        if (vx > 0) {
            return (1.0 - rx - radius) / vx;
        } else if (vx < 0) {
            return (radius - rx) / vx;
        } else {
            return INFINITY;
        }
    }
    
    /**
     * 撞到水平墙体所需时间
     *
     * @return 时间
     */
    public double timeToHitHorizontalWall() {
        if (vy > 0) {
            return (1.0 - ry - radius) / vy;
        } else if (vy < 0) {
            return (radius - ry) / vy;
        } else {
            return INFINITY;
        }
    }
    
    
    //假设是弹性碰撞,处理碰撞后的结果
    public void bounceOff(Particle that) {
        double dx = that.rx - this.rx;
        double dy = that.ry - this.ry;
        double dvx = that.vx - this.vx;
        double dvy = that.vy - this.vy;
        double dvdr = dx * dvx + dy * dvy;             // dv dot dr
        double dist = this.radius + that.radius;   // distance between particle centers at collison
        
        // magnitude of normal force
        double magnitude = 2 * this.mass * that.mass * dvdr / ((this.mass + that.mass) * dist);
        
        // normal force, and in x and y directions
        double fx = magnitude * dx / dist;
        double fy = magnitude * dy / dist;
        
        // update velocities according to normal force
        this.vx += fx / this.mass;
        this.vy += fy / this.mass;
        that.vx -= fx / that.mass;
        that.vy -= fy / that.mass;
        
        // update collision counts
        this.count++;
        that.count++;
    }
    
    //撞垂直墙体后的速度
    public void bounceOffVerticalWall() {
        vx = -vx;
        count++;
    }
    
    //撞水平墙体后的速度
    public void bounceOffHorizontalWall() {
        vy = -vy;
        count++;
    }
    
    //计算当前粒子的动能
    public double kineticEnergy() {
        return 0.5 * mass * (vx * vx + vy * vy);
    }
    
}
