public class HabitacionSuperior extends Habitacion {
    private boolean tieneBalcon;

    public HabitacionSuperior(String numero, int capacidad, double precioNoche, boolean tieneBalcon) {
        super(numero, capacidad, precioNoche);
        this.tieneBalcon = tieneBalcon;
    }

    public boolean isTieneBalcon() { return tieneBalcon; }
    public void setTieneBalcon(boolean tieneBalcon) { this.tieneBalcon = tieneBalcon; }

    @Override
    public String getCategoria() {
        return "Superior";
    }
}