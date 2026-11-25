package grupofp.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "hoteles")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hotel")
    private int idHotel;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "ciudad")
    private String ciudad;

    public Hotel() {}

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