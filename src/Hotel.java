public class Hotel {
    private int idHotel;
    private String nombre;
    private String ciudad;

    public Hotel(int idHotel, String nombre, String ciudad) {
        this.idHotel = idHotel;
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    public int getIdHotel() { return idHotel; }
    public void setIdHotel(int idHotel) { this.idHotel = idHotel; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    @Override
    public String toString() {
        return "Hotel: " + nombre + " en " + ciudad;
    }
}