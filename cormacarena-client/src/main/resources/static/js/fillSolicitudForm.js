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

            Swal.fire ({
                icon: 'info',
                title: 'Borrador Encontrado',
                text: 'Se ha encontrado un borrador pendiente de envío asociado a su número de documento.',
                confirmButtonColor: '#3085d6'
            }).then(async () => {
                await cargarSolicitudes(idSolicitante);
            })

        } catch (error) {
                const formulario = document.getElementById("request-licence-form");
                if (formulario) formulario.reset();

                const soporteEIA = document.getElementById("nombreSoporteEIA");
                const nombreDocumento = document.getElementById("nombreDocumentoActual");

                if (soporteEIA) soporteEIA.textContent = "";
                if (nombreDocumento) nombreDocumento.classList.add("hidden");

                await cargarSolicitudes(idSolicitante);
            console.error(error);
        }
    });
});


const cargarSolicitudes = async (idSolicitante) => {
    try {
        const resp = await fetch(`/solicitudes/${idSolicitante}`);
        if (!resp.ok) throw new Error("Error al obtener solicitudes");

        const solicitudes = await resp.json();
        const contenedor = document.getElementById("tablaSolicitudesContainer");
        const divSolicitudes = document.getElementById("divSolicitudes");
        const textoId = document.getElementById("textoIdSolicitante");

        textoId.textContent = idSolicitante;

        divSolicitudes.classList.remove("hidden");

        if (solicitudes.length === 0) {
            contenedor.innerHTML = `<div class="text-center text-gray-500 mt-4">No hay solicitudes registradas.</div>`;
        } else {
            const filas = solicitudes.map(s => `
                <tr class="even:bg-gray-100 hover:bg-primary/20 transition-colors duration-300 cursor-pointer">
                    <td class="py-2 px-4 border border-gray-300">${s.codigoSolicitud}</td>
                    <td class="py-2 px-4 border border-gray-300">${s.nombreProyecto}</td>
                    <td class="py-2 px-4 border border-gray-300">${s.estado}</td>
                </tr>
            `).join("");

            contenedor.innerHTML = `
                <table class="w-full text-center border-collapse">
                    <thead>
                        <tr class="bg-primary text-white">
                            <th class="py-3 px-4 border border-primary">ID</th>
                            <th class="py-3 px-4 border border-primary">Nombre del Proyecto</th>
                            <th class="py-3 px-4 border border-primary">Estado</th>
                        </tr>
                    </thead>
                    <tbody>${filas}</tbody>
                </table>
            `;
        }

    } catch (e) {
        console.error("Error cargando solicitudes:", e);
    }
};

