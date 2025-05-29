class MensajeAlerta {
    static mostrar(tipo, titulo, mensaje) {
        Swal.fire({
            icon: tipo,
            title: titulo,
            text: mensaje,
            confirmButtonColor: tipo === 'error' ? '#d33' : '#3085d6'
        });
    }

    static mostrarSiExiste(variable, tipo, titulo) {
        if (typeof variable !== 'undefined' && variable?.trim()) {
            this.mostrar(tipo, titulo, variable);
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {
    MensajeAlerta.mostrarSiExiste(mensajeError, 'error', '¡Error!');
    MensajeAlerta.mostrarSiExiste(mensajeExito, 'success', '¡Éxito!');
});
