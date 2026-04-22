package co.edu.upb.veterinaria.services.ServicioProducto;

import co.edu.upb.veterinaria.models.ModeloAccesorio.Accesorio;
import co.edu.upb.veterinaria.models.ModeloAlimento.Alimento;
import co.edu.upb.veterinaria.models.ModeloMarca.Marca;
import co.edu.upb.veterinaria.models.ModeloMaterialQuirurgico.MaterialQuirurgico;
import co.edu.upb.veterinaria.models.ModeloMedicamento.Medicamento;
import co.edu.upb.veterinaria.models.ModeloProducto.Producto;
import co.edu.upb.veterinaria.models.ModeloProveedor.Proveedor;
import co.edu.upb.veterinaria.models.ModeloUnidadMedida.UnidadMedida;
import co.edu.upb.veterinaria.models.ModeloUsuario.Usuario;
import co.edu.upb.veterinaria.repositories.RepositorioAccesorio.AccesorioRepository;
import co.edu.upb.veterinaria.repositories.RepositorioAlimento.AlimentoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioMaterialQuirurgico.MaterialQuirurgicoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioMedicamento.MedicamentoRepository;
import co.edu.upb.veterinaria.repositories.RepositorioProducto.ProductoRepository;
import co.edu.upb.veterinaria.services.ServicioTipoProducto.TipoProductoService;

import java.sql.SQLException;
import java.util.*;

/**
 * ProductoService
 * ---------------
 * Servicio de LÓGICA DE NEGOCIO para productos.
 *
 * Responsabilidades:
 *  - Validación de datos de negocio (campos requeridos, rangos, fechas, etc.)
 *  - Orquestación de repositorios especializados según tipo de producto
 *  - Manejo transaccional para operaciones complejas
 *  - Conversión entre modelos de vista y modelos de persistencia
 *
 * IMPORTANTE: No hay tablas separadas para cada tipo de producto en BD.
 * Todo se guarda en veterinaria.producto, diferenciado por tipoproducto_idtipoproducto.
 * Los repositorios especializados (MedicamentoRepository, AlimentoRepository, etc.)
 * filtran por tipo y manejan los campos específicos de cada uno.
 *
 * ✅ ACTUALIZADO: Ahora usa TipoProductoService para obtener IDs dinámicamente desde BD.
 */
public class ProductoService {

    // ============================================================
    //                    REPOSITORIOS
    // ============================================================

    private final ProductoRepository productoRepo;
    private final MedicamentoRepository medicamentoRepo;
    private final AlimentoRepository alimentoRepo;
    private final MaterialQuirurgicoRepository materialQuirurgicoRepo;
    private final AccesorioRepository accesorioRepo;
    private final TipoProductoService tipoProductoService;

    // ============================================================
    //                    CONSTRUCTOR
    // ============================================================

    public ProductoService() {
        this.productoRepo = new ProductoRepository();
        this.medicamentoRepo = new MedicamentoRepository();
        this.alimentoRepo = new AlimentoRepository();
        this.materialQuirurgicoRepo = new MaterialQuirurgicoRepository();
        this.accesorioRepo = new AccesorioRepository();
        this.tipoProductoService = new TipoProductoService();
    }

    // ============================================================
    //      REGISTRO DE PRODUCTOS (según tipo)
    // ============================================================

    /**
     * Registra un MEDICAMENTO en el inventario.
     *
     * Campos requeridos:
     *  - Nombre, referencia, código de barras, precio, costo, stock, marca, estado, imagen
     *  - lote, fechaVencimiento, semanasParaAlerta, contenido, dosisPorUnidad, unidadMedida
     *
     * Campos opcionales:
     *  - fraccionable, fraccionado, descripcion, proveedor
     *
     * @return ID del producto creado
     * @throws IllegalArgumentException si faltan campos requeridos o son inválidos
     * @throws SQLException si hay error en BD
     */
    public int registrarMedicamento(
            String nombre,
            String referencia,
            String codigoBarras,  // REQUERIDO - el usuario lo ingresa manualmente
            double precio,
            double costo,
            int stock,
            Marca marca,
            UnidadMedida unidadMedida,
            Proveedor proveedor,  // opcional
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario,
            // Campos específicos de medicamento
            String lote,
            Date fechaVencimiento,
            int semanasParaAlerta,
            boolean fraccionable,
            boolean fraccionado,
            double contenido,
            double dosisPorUnidad
    ) throws SQLException {

        // ===== VALIDACIONES DE NEGOCIO =====
        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("El lote es requerido para medicamentos");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida para medicamentos");
        }
        if (fechaVencimiento.before(new Date())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }
        if (semanasParaAlerta < 1) {
            throw new IllegalArgumentException("Las semanas para alerta deben ser al menos 1");
        }
        if (contenido <= 0) {
            throw new IllegalArgumentException("El contenido debe ser mayor a 0");
        }
        if (dosisPorUnidad < 0) {
            throw new IllegalArgumentException("La dosis por unidad no puede ser negativa");
        }
        if (unidadMedida == null) {
            throw new IllegalArgumentException("La unidad de medida es requerida para medicamentos");
        }

        // ===== CREAR MODELO MEDICAMENTO =====
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(nombre);
        medicamento.setReferencia(referencia);
        medicamento.setCodigoBarras(codigoBarras);
        medicamento.setPrecio(precio);
        medicamento.setCosto(costo);
        medicamento.setStock(stock);
        medicamento.setMarca(marca);
        medicamento.setDescripcion(descripcion != null ? descripcion : "");
        medicamento.setEstado(estado);
        medicamento.setImagenProducto(imagenProducto);
        medicamento.setUsuario(usuario);
        medicamento.setProveedor(proveedor);

        // Campos específicos
        medicamento.setLote(lote);
        medicamento.setFechaVencimiento(fechaVencimiento);
        medicamento.setSemanasParaAlerta(semanasParaAlerta);
        medicamento.setFraccionable(fraccionable);
        medicamento.setFraccionado(fraccionado);
        medicamento.setContenido(contenido);
        medicamento.setUnidadmedida(unidadMedida);
        medicamento.setDosisPorUnidad(dosisPorUnidad);

        // ===== PERSISTIR EN BD =====
        // El MedicamentoRepository se encarga de guardar en veterinaria.producto
        // con tipoproducto_idtipoproducto = 2 (MEDICAMENTO)
        return medicamentoRepo.create(medicamento);
    }

    /**
     * Registra un ALIMENTO en el inventario.
     *
     * Campos requeridos:
     *  - Nombre, referencia, código de barras, precio, costo, stock, marca, estado, imagen
     *  - lote, fechaVencimiento, semanasParaAlerta, contenido, unidadMedida
     */
    public int registrarAlimento(
            String nombre,
            String referencia,
            String codigoBarras,  // REQUERIDO
            double precio,
            double costo,
            int stock,
            Marca marca,
            UnidadMedida unidadMedida,
            Proveedor proveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario,
            // Campos específicos de alimento
            String lote,
            Date fechaVencimiento,
            int semanasParaAlerta,
            double contenido
    ) throws SQLException {

        // ===== VALIDACIONES =====
        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("El lote es requerido para alimentos");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida para alimentos");
        }
        if (fechaVencimiento.before(new Date())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }
        if (semanasParaAlerta < 1) {
            throw new IllegalArgumentException("Las semanas para alerta deben ser al menos 1");
        }
        if (contenido <= 0) {
            throw new IllegalArgumentException("El contenido debe ser mayor a 0");
        }
        if (unidadMedida == null) {
            throw new IllegalArgumentException("La unidad de medida es requerida para alimentos");
        }

        // ===== CREAR MODELO ALIMENTO =====
        Alimento alimento = new Alimento();
        alimento.setNombre(nombre);
        alimento.setReferencia(referencia);
        alimento.setCodigoBarras(codigoBarras);
        alimento.setPrecio(precio);
        alimento.setCosto(costo);
        alimento.setStock(stock);
        alimento.setMarca(marca);
        alimento.setDescripcion(descripcion != null ? descripcion : "");
        alimento.setEstado(estado);
        alimento.setImagenProducto(imagenProducto);
        alimento.setUsuario(usuario);
        alimento.setProveedor(proveedor);

        // Campos específicos
        alimento.setLote(lote);
        alimento.setFechaVencimiento(fechaVencimiento);
        alimento.setSemanasParaAlerta(semanasParaAlerta);
        alimento.setContenido(contenido);
        alimento.setUnidadMedida(unidadMedida);

        // ===== PERSISTIR =====
        // El AlimentoRepository se encarga de guardar en veterinaria.producto
        // con tipoproducto_idtipoproducto = 1 (ALIMENTO)
        return alimentoRepo.create(alimento);
    }

    /**
     * Registra un MATERIAL QUIRÚRGICO en el inventario.
     *
     * Campos requeridos:
     *  - Nombre, referencia, código de barras, precio, costo, stock, marca, estado, imagen
     *  - lote, semanasParaAlerta, contenido
     *
     * Campos opcionales:
     *  - fechaVencimiento (algunos materiales no vencen)
     *  - fraccionable, fraccionado, unidadMedida
     */
    public int registrarMaterialQuirurgico(
            String nombre,
            String referencia,
            String codigoBarras,  // REQUERIDO
            double precio,
            double costo,
            int stock,
            Marca marca,
            UnidadMedida unidadMedida,  // opcional
            Proveedor proveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario,
            // Campos específicos
            String lote,
            Date fechaVencimiento,  // OPCIONAL
            int semanasParaAlerta,
            boolean fraccionable,
            boolean fraccionado,
            double contenido
    ) throws SQLException {

        // ===== VALIDACIONES =====
        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        if (lote == null || lote.isBlank()) {
            throw new IllegalArgumentException("El lote es requerido para material quirúrgico");
        }
        if (semanasParaAlerta < 1) {
            throw new IllegalArgumentException("Las semanas para alerta deben ser al menos 1");
        }
        if (contenido <= 0) {
            throw new IllegalArgumentException("El contenido debe ser mayor a 0");
        }
        // Validar fecha de vencimiento solo si viene
        if (fechaVencimiento != null && fechaVencimiento.before(new Date())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }

        // ===== CREAR MODELO MATERIAL QUIRÚRGICO =====
        MaterialQuirurgico material = new MaterialQuirurgico();
        material.setNombre(nombre);
        material.setReferencia(referencia);
        material.setCodigoBarras(codigoBarras);
        material.setPrecio(precio);
        material.setCosto(costo);
        material.setStock(stock);
        material.setMarca(marca);
        material.setDescripcion(descripcion != null ? descripcion : "");
        material.setEstado(estado);
        material.setImagenProducto(imagenProducto);
        material.setUsuario(usuario);
        material.setProveedor(proveedor);

        // Campos específicos
        material.setLote(lote);
        material.setFechaVencimiento(fechaVencimiento);
        material.setSemanaParaAlerta(semanasParaAlerta);
        material.setFraccionable(fraccionable);
        material.setFraccionado(fraccionado);
        material.setContenido(contenido);
        material.setUnidadMedida(unidadMedida);

        // ===== PERSISTIR =====
        // El MaterialQuirurgicoRepository se encarga de guardar en veterinaria.producto
        // con tipoproducto_idtipoproducto = 3 (MATERIAL QUIRÚRGICO)
        return materialQuirurgicoRepo.create(material);
    }

    /**
     * Registra un ACCESORIO/JUGUETE en el inventario.
     *
     * Solo usa campos base (sin lote, sin vencimiento, sin campos específicos).
     *
     * Campos requeridos:
     *  - Nombre, referencia, código de barras, precio, costo, stock, marca, estado, imagen
     */
    public int registrarAccesorio(
            String nombre,
            String referencia,
            String codigoBarras,  // REQUERIDO
            double precio,
            double costo,
            int stock,
            Marca marca,
            Proveedor proveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario
    ) throws SQLException {

        // ===== VALIDACIONES =====
        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        // ===== CREAR MODELO ACCESORIO =====
        Accesorio accesorio = new Accesorio();
        accesorio.setNombre(nombre);
        accesorio.setReferencia(referencia);
        accesorio.setCodigoBarras(codigoBarras);
        accesorio.setPrecio(precio);
        accesorio.setCosto(costo);
        accesorio.setStock(stock);
        accesorio.setMarca(marca);
        accesorio.setDescripcion(descripcion != null ? descripcion : "");
        accesorio.setEstado(estado);
        accesorio.setImagenProducto(imagenProducto);
        accesorio.setUsuario(usuario);
        accesorio.setProveedor(proveedor);

        // ===== PERSISTIR =====
        // El AccesorioRepository se encarga de guardar en veterinaria.producto
        // con tipoproducto_idtipoproducto = 4 (ACCESORIO)
        return accesorioRepo.create(accesorio);
    }

    // ============================================================
    //              ACTUALIZACIÓN DE PRODUCTOS
    // ============================================================

    /**
     * Actualiza un medicamento existente.
     */
    public boolean actualizarMedicamento(
            int idProducto,
            String nombre,
            String referencia,
            String codigoBarras,
            double precio,
            double costo,
            int stock,
            Marca marca,
            UnidadMedida unidadMedida,
            Integer idProveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario,
            String lote,
            Date fechaVencimiento,
            int semanasParaAlerta,
            boolean fraccionable,
            boolean fraccionado,
            double contenido,
            double dosisPorUnidad
    ) throws SQLException {

        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        Medicamento medicamento = new Medicamento();
        medicamento.setIdProducto(idProducto);
        medicamento.setNombre(nombre);
        medicamento.setReferencia(referencia);
        medicamento.setCodigoBarras(codigoBarras);
        medicamento.setPrecio(precio);
        medicamento.setCosto(costo);
        medicamento.setStock(stock);
        medicamento.setMarca(marca);
        medicamento.setDescripcion(descripcion);
        medicamento.setEstado(estado);
        medicamento.setImagenProducto(imagenProducto);
        medicamento.setUsuario(usuario);

        medicamento.setLote(lote);
        medicamento.setFechaVencimiento(fechaVencimiento);
        medicamento.setSemanasParaAlerta(semanasParaAlerta);
        medicamento.setFraccionable(fraccionable);
        medicamento.setFraccionado(fraccionado);
        medicamento.setContenido(contenido);
        medicamento.setUnidadmedida(unidadMedida);
        medicamento.setDosisPorUnidad(dosisPorUnidad);

        return medicamentoRepo.update(medicamento, idProveedor);
    }

    /**
     * Actualiza un alimento existente.
     */
    public boolean actualizarAlimento(
            int idProducto,
            String nombre,
            String referencia,
            String codigoBarras,
            double precio,
            double costo,
            int stock,
            Marca marca,
            UnidadMedida unidadMedida,
            Integer idProveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario,
            String lote,
            Date fechaVencimiento,
            int semanasParaAlerta,
            double contenido
    ) throws SQLException {

        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        Alimento alimento = new Alimento();
        alimento.setIdProducto(idProducto);
        alimento.setNombre(nombre);
        alimento.setReferencia(referencia);
        alimento.setCodigoBarras(codigoBarras);
        alimento.setPrecio(precio);
        alimento.setCosto(costo);
        alimento.setStock(stock);
        alimento.setMarca(marca);
        alimento.setDescripcion(descripcion);
        alimento.setEstado(estado);
        alimento.setImagenProducto(imagenProducto);
        alimento.setUsuario(usuario);

        alimento.setLote(lote);
        alimento.setFechaVencimiento(fechaVencimiento);
        alimento.setSemanasParaAlerta(semanasParaAlerta);
        alimento.setContenido(contenido);
        alimento.setUnidadMedida(unidadMedida);

        return alimentoRepo.update(alimento, idProveedor);
    }

    /**
     * Actualiza un material quirúrgico existente.
     */
    public boolean actualizarMaterialQuirurgico(
            int idProducto,
            String nombre,
            String referencia,
            String codigoBarras,
            double precio,
            double costo,
            int stock,
            Marca marca,
            UnidadMedida unidadMedida,
            Integer idProveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario,
            String lote,
            Date fechaVencimiento,
            int semanasParaAlerta,
            boolean fraccionable,
            boolean fraccionado,
            double contenido
    ) throws SQLException {

        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        MaterialQuirurgico material = new MaterialQuirurgico();
        material.setIdProducto(idProducto);
        material.setNombre(nombre);
        material.setReferencia(referencia);
        material.setCodigoBarras(codigoBarras);
        material.setPrecio(precio);
        material.setCosto(costo);
        material.setStock(stock);
        material.setMarca(marca);
        material.setDescripcion(descripcion);
        material.setEstado(estado);
        material.setImagenProducto(imagenProducto);
        material.setUsuario(usuario);

        material.setLote(lote);
        material.setFechaVencimiento(fechaVencimiento);
        material.setSemanaParaAlerta(semanasParaAlerta);
        material.setFraccionable(fraccionable);
        material.setFraccionado(fraccionado);
        material.setContenido(contenido);
        material.setUnidadMedida(unidadMedida);

        return materialQuirurgicoRepo.update(material, idProveedor);
    }

    /**
     * Actualiza un accesorio existente.
     */
    public boolean actualizarAccesorio(
            int idProducto,
            String nombre,
            String referencia,
            String codigoBarras,
            double precio,
            double costo,
            int stock,
            Marca marca,
            Integer idProveedor,
            String descripcion,
            String estado,
            byte[] imagenProducto,
            Usuario usuario
    ) throws SQLException {

        validarCamposBase(nombre, referencia, codigoBarras, precio, costo, stock, marca, estado, imagenProducto);

        Accesorio accesorio = new Accesorio();
        accesorio.setIdProducto(idProducto);
        accesorio.setNombre(nombre);
        accesorio.setReferencia(referencia);
        accesorio.setCodigoBarras(codigoBarras);
        accesorio.setPrecio(precio);
        accesorio.setCosto(costo);
        accesorio.setStock(stock);
        accesorio.setMarca(marca);
        accesorio.setDescripcion(descripcion);
        accesorio.setEstado(estado);
        accesorio.setImagenProducto(imagenProducto);
        accesorio.setUsuario(usuario);

        return accesorioRepo.update(accesorio, idProveedor);
    }

    /**
     * Elimina un producto FÍSICAMENTE de la base de datos.
     *
     * ⚠️ ADVERTENCIA: Esta operación elimina el producto permanentemente.
     * NO se puede deshacer. El producto y todas sus relaciones se pierden.
     *
     * Antes de eliminar:
     * - Se eliminan automáticamente sus vínculos con proveedores
     * - Verifica que no haya registros dependientes en otras tablas
     *
     * @param idProducto ID del producto a eliminar
     * @return true si se eliminó correctamente, false si no existía
     * @throws SQLException si hay error de BD o violación de integridad referencial
     */
    public boolean eliminarProducto(int idProducto) throws SQLException {
        return productoRepo.delete(idProducto);
    }

    /**
     * Busca un producto por ID.
     */
    public Optional<Producto> buscarProductoPorId(int idProducto) throws SQLException {
        return productoRepo.findById(idProducto);
    }

    /**
     * Busca productos con filtros (para inventario general).
     */
    public List<Producto> buscarProductos(
            String q,
            String estadoFilter,
            Integer stockMinimo,
            Integer stockMaximo,
            String sort,
            boolean asc,
            int limit,
            int offset
    ) throws SQLException {
        return productoRepo.search(q, estadoFilter, sort, asc, limit, offset);
    }

    /**
     * Cuenta productos with filters.
     */
    public long contarProductos(
            String q,
            String estadoFilter
    ) throws SQLException {
        return productoRepo.count(q, estadoFilter);
    }

    // ============================================================
    //              VALIDACIONES PRIVADAS
    // ============================================================

    /**
     * Valida campos base comunes a todos los productos.
     */
    private void validarCamposBase(
            String nombre,
            String referencia,
            String codigoBarras,
            double precio,
            double costo,
            int stock,
            Marca marca,
            String estado,
            byte[] imagenProducto
    ) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto es requerido");
        }
        if (referencia == null || referencia.isBlank()) {
            throw new IllegalArgumentException("La referencia del producto es requerida");
        }
        if (codigoBarras == null || codigoBarras.isBlank()) {
            throw new IllegalArgumentException("El código de barras es requerido");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (costo < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo");
        }
        if (costo > precio) {
            throw new IllegalArgumentException("El costo no puede ser mayor al precio");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        if (marca == null) {
            throw new IllegalArgumentException("La marca es requerida");
        }
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }
        if (!estado.equals("ACTIVO") && !estado.equals("INACTIVO")) {
            throw new IllegalArgumentException("El estado debe ser ACTIVO o INACTIVO");
        }
        if (imagenProducto == null || imagenProducto.length == 0) {
            throw new IllegalArgumentException("La imagen del producto es requerida");
        }
    }

    // ============================================================
    //              UTILIDADES
    // ============================================================

    /**
     * Verifica si un código de barras ya existe.
     * Útil para validar antes de registrar/actualizar.
     */
    public boolean existeCodigoBarras(String codigoBarras) throws SQLException {
        return productoRepo.existsByCodigoBarras(codigoBarras);
    }

    /**
     * Verifica si una referencia ya existe.
     * Útil para validar antes de registrar/actualizar.
     */
    public boolean existeReferencia(String referencia) throws SQLException {
        return productoRepo.existsByReferencia(referencia);
    }
}
