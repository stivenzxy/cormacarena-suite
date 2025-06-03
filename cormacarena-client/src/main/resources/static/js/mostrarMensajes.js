class MensajeAlerta {
    static mostrar(tipo, titulo, mensaje) {
        Swal.fire({
            toast: true,
            icon: tipo,
            title: titulo,
            text: mensaje,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            background: '#fff',
            color: '#000',
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
