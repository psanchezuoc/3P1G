package grupofp.modelo;

public class Suite extends Habitacion {
    private boolean tieneJacuzzi;

    public Suite(String numero, int capacidad, double precioNoche, boolean tieneJacuzzi) {
        super(numero, capacidad, precioNoche);
        this.tieneJacuzzi = tieneJacuzzi;
    }

    public boolean isTieneJacuzzi() { return tieneJacuzzi; }
    public void setTieneJacuzzi(boolean tieneJacuzzi) { this.tieneJacuzzi = tieneJacuzzi; }

    @Override
    public String getCategoria() {
        return "Suite";
    }
}