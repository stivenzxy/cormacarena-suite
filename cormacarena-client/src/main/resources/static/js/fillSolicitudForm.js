document.addEventListener("DOMContentLoaded", () => {
    let buscarBtn = document.getElementById("btnBuscarSolicitante");

    if (!buscarBtn) return;

    buscarBtn.addEventListener("click", async () => {
        const idSolicitante = document.getElementById("idSolicitante").value.trim();
        if (!idSolicitante) return;

        try {
            const response = await fetch(`/solicitud-borrador/${idSolicitante}`);
            if (!response.ok) throw new Error("No encontrado");

            const data = await response.json();

            document.getElementById("nombreSolicitante").value = data.nombreSolicitante || "";
            document.getElementById("tipoIdentificacion").value = data.tipoIdentificacion || "";
            document.getElementById("telefono").value = data.telefono || "";
            document.getElementById("email").value = data.email || "";
            document.getElementById("direccionResidencia").value = data.direccionResidencia || "";
            document.getElementById("nombreProyecto").value = data.nombreProyecto || "";
            document.getElementById("sectorProyecto").value = data.sectorProyecto || "";
            document.getElementById("valorProyecto").value = data.valorProyecto || "";
            document.getElementById("departamentoProyecto").value = data.departamentoProyecto || "";
            document.getElementById("municipioProyecto").value = data.municipioProyecto || "";
            document.getElementById("nombreSoporteEIA").textContent = data.nombreSoporteEIA || "";
            document.getElementById("nombreDocumentoActual").classList.remove("hidden");

            Swal.fire({
                toast: true,
                icon: 'info',
                title: 'Borrador Encontrado',
                text: 'Se ha encontrado un borrador pendiente de envío asociado a su número de documento.',
                position: 'top-end',
                showConfirmButton: false,
                timer: 5000,
                timerProgressBar: true
            });

        } catch (error) {
            Swal.fire({
                toast: true,
                icon: 'error',
                title: 'No hay borradores guardados',
                text: 'Llene el formulario para solicitar una licencia.',
                position: 'top-end',
                showConfirmButton: false,
                timer: 5000,
                timerProgressBar: true
            });
            console.error(error);
        }
    });
});