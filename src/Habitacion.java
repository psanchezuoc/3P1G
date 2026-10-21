public abstract class Habitacion {
    protected String numero;
    protected int capacidad;
    protected double precioNoche;

    public Habitacion(String numero, int capacidad, double precioNoche) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.precioNoche = precioNoche;
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public double getPrecioNoche() { return precioNoche; }
    public void setPrecioNoche(double precioNoche) { this.precioNoche = precioNoche; }

    public abstract String getCategoria();
}