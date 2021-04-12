package projetdysgraphie;

/**
 * Représentation de la classe Point spécifiquement utilisée pour l'affichage de points.
 */
public class PointAffichage {
    private double x;
    private double y;
    
    public PointAffichage(double x, double y){
        this.x=x;
        this.y=y;
    }
    public PointAffichage(Point p){
        x=p.getX();
        y=p.getY();
    }
    
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public void setX(double x){
        this.x=x;
    }
    public void setY(double y){
        this.y=y;
    }
    
    
    @Override
    public String toString(){
        return "("+x+";"+y+")";
    }
}
