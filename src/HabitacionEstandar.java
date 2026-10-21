public class HabitacionEstandar extends Habitacion {
    private String tipoCama;

    public HabitacionEstandar(String numero, int capacidad, double precioNoche, String tipoCama) {
        super(numero, capacidad, precioNoche);
        this.tipoCama = tipoCama;
    }

    public String getTipoCama() { return tipoCama; }
    public void setTipoCama(String tipoCama) { this.tipoCama = tipoCama; }

    @Override
    public String getCategoria() {
        return "Estándar";
    }
}