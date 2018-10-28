package app;
public class Pair{
	int a = -1,b = -1;
	public Pair(int a, int b) {this.a = a; this.b = b;}
	@Override
	public boolean equals(Object o ){
		if(!(o instanceof Pair))
			return false;
		else return ((Pair)o).a==this.a && ((Pair) o).b==this.b;
	}
	@Override
	public int hashCode(){
		return a+b;
	}
}