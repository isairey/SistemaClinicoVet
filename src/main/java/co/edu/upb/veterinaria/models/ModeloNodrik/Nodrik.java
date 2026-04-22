package co.edu.upb.veterinaria.models.ModeloNodrik;

public class Nodrik {
    private boolean reporteVentasPeriodico;
    private String periodoReporteVentas;

    public Nodrik() {
    }

    public Nodrik(boolean reporteVentasPeriodico, String periodoReporteVentas) {
        this.reporteVentasPeriodico = reporteVentasPeriodico;
        this.periodoReporteVentas = periodoReporteVentas;
    }

    public boolean isReporteVentasPeriodico() {
        return reporteVentasPeriodico;
    }

    public void setReporteVentasPeriodico(boolean reporteVentasPeriodico) {
        this.reporteVentasPeriodico = reporteVentasPeriodico;
    }

    public String getPeriodoReporteVentas() {
        return periodoReporteVentas;
    }

    public void setPeriodoReporteVentas(String periodoReporteVentas) {
        this.periodoReporteVentas = periodoReporteVentas;
    }
}
