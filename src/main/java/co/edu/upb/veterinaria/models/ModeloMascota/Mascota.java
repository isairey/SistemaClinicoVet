package co.edu.upb.veterinaria.models.ModeloMascota;

import co.edu.upb.veterinaria.models.ModeloCliente.Cliente;

public class Mascota {
    private int idMascota;
    private String nombre;
    private String raza;
    private String especie;
    private Cliente responsable;
    private String numeroChip;
    private char sexo;
    private int edad;

    public Mascota() { }

    public Mascota(int idMascota, String nombre, String raza, String especie,
                   Cliente responsable, String numeroChip,
                   char sexo, int edad) {
        this.idMascota = idMascota;
        this.nombre = nombre;
        this.raza = raza;
        this.especie = especie;
        this.responsable = responsable;
        this.numeroChip = numeroChip;
        this.sexo = sexo;
        this.edad = edad;
    }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public Cliente getResponsable() { return responsable; }
    public void setResponsable(Cliente responsable) { this.responsable = responsable; }

    public String getNumeroChip() { return numeroChip; }
    public void setNumeroChip(String numeroChip) { this.numeroChip = numeroChip; }

    public char getSexo() { return sexo; }
    public void setSexo(char sexo) { this.sexo = sexo; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
}
