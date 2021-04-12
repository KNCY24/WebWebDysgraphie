package projetdysgraphie;

public class Point implements Comparable {
    private int x;
    private int y;
    private int num;
    private int inter;
    private int tps;
    
    public Point(int x, int y, int num, int inter, int tps){
        this.x = x;
        this.y = y;
        this.inter = inter;
        this.num = num;
        this.tps = tps;
    }
    
    /**
     * Calcule la distance entre 2 points
     * @param p
     * @return la distance entre 2 points
     */
    public double distanceAvec(Point p){
        return Math.sqrt(Math.pow(p.x-this.x,2)+Math.pow(p.y-this.y, 2));
    }
    
    /**
     * Calcule la vitesse entre this et le point p
     * @param p
     * @return vitesse
     */
    public double vitesseEntre(Point p){
        int interval;
        if(this.num>p.num){
            interval = p.inter;
        }
        else{
            interval = this.inter;
        }
        return p.distanceAvec(this)/((double)interval);
    }
    
    public int IntervalleEntre(Point pAv) {
        int inter;
        inter = this.tps - pAv.tps;
        return inter;
    }
    
    public int getInterval(){
        return inter;
    }
    
    public int getTime() {
        return tps;
    }
    
    public int getNum() {
        return num;
    }
    
    @Override
    public int compareTo(Object o) {
        Point p = (Point)o;
        return this.num-p.num;
    }
    
    @Override
    public String toString(){
        return "N°"+num+"\tx:"+x+"\ty:"+y+"\tinter:"+inter;
    }
    
    public int getX(){
	return this.x;
    }
 
    public int getY(){
	return this.y;
    }
    
    public void setInter(int inter){
        this.inter = inter;
    }
    
    /**
     * transforme un Point en PointAffichage
     * TODO: fusionner la classe Point et PointAffichage
     * @return 
     */
    public PointAffichage toPointAffichage(){
        return new PointAffichage((double)x, (double)y);
    }
}
