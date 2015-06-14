package com.bondfire.swiftyglider.ui;


public  class Box {

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    public boolean contains(float x, float y){
        return x > this.x - width /2 &&
                x < this.x + width/2 &&
                y > this.y - height /2 &&
                y < this.y + height /2;
    }

    public boolean colliding(float x, float y, float width, float height) {

//        System.out.println("GliderX:" + x + " WallX: " +this.x  + " WallWidth: " + this.width);

        return x + width/2> this.x - this.width / 2 &&
                x - width/2< this.x + this.width/2 &&
                y + height / 2 > this.y - this.height / 2 &&
                y - height / 2 < this.y + this.height / 2;
    }
}
