document.addEventListener("DOMContentLoaded", () => {
    let buscarBtn = document.getElementById("btnBuscarSolicitante");

    if (!buscarBtn) return;

    buscarBtn.addEventListener("click", async () => {
        const id = document.getElementById("idSolicitante").value.trim();
        if (!id) return;

        try {
            const response = await fetch(`/solicitudes/${id}`);
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

        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: 'Solicitante no encontrado',
                text: 'No se encontró un solicitante con ese número de identificación.',
            });
            console.error(error);
        }
    });
});
