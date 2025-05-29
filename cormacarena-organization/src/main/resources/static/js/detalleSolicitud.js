let solicitudSeleccionada = null;

function cargarDetalle(codigoSolicitud) {
    solicitudSeleccionada = codigoSolicitud;
    fetch('/evaluacion-solicitud/' + codigoSolicitud)
        .then(response => response.json())
        .then(data => {
            document.getElementById('modalContent').innerHTML = `
                <div class="flex flex-col sm:grid-cols-2 gap-6 text-sm text-gray-700">
                    <div>
                        <h4 class="text-base font-semibold text-blue-900 mb-2 border-b pb-1">Información del Solicitante</h4>
                        <p><span class="font-medium">Nombre:</span> ${data.nombreSolicitante}</p>
                        <p><span class="font-medium">Tipo de Identificación:</span> ${data.tipoIdentificacion}</p>
                        <p><span class="font-medium">ID del Solicitante:</span> ${data.idSolicitante}</p>
                        <p><span class="font-medium">Teléfono:</span> ${data.telefono}</p>
                        <p><span class="font-medium">Email:</span> ${data.email}</p>
                        <p><span class="font-medium">Dirección:</span> ${data.direccionResidencia}</p>
                    </div>
                    <div>
                        <h4 class="text-base font-semibold text-blue-900 mb-2 border-b pb-1">Información del Proyecto</h4>
                        <p><span class="font-medium">Nombre del Proyecto:</span> ${data.nombreProyecto}</p>
                        <p><span class="font-medium">Sector:</span> ${data.sectorProyecto}</p>
                        <p><span class="font-medium">Valor:</span> $${data.valorProyecto?.toLocaleString('es-CO')}</p>
                        <p><span class="font-medium">Departamento:</span> ${data.departamentoProyecto}</p>
                        <p><span class="font-medium">Municipio:</span> ${data.municipioProyecto}</p>
                        <p><span class="font-medium">Estado:</span> ${data.estado || 'Pendiente'}</p>
                        <p><span class="font-medium">Soporte adjunto:</span> ${data.nombreSoporteEIA}</p>
                    </div>
                </div>
            `;
            document.getElementById('detalleModal').classList.remove('hidden');
        })
        .catch(error => {
            console.error("Error al obtener detalles:", error);
            document.getElementById('modalContent').innerHTML = `<p class="text-red-500">Error al cargar los datos.</p>`;
            document.getElementById('detalleModal').classList.remove('hidden');
        });
}

function enviarAccion(endpoint) {
    if (!solicitudSeleccionada) return;

    console.log("IdProcess: ", solicitudSeleccionada)

    fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `processId=${encodeURIComponent(solicitudSeleccionada)}`
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Error al ejecutar la acción");
            }
            return response.text();
        })
        .then(() => {
            cerrarModal();
            location.reload(); // Recarga la página
        })
        .catch(error => {
            alert("Error: " + error.message);
        });
}

document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("btnAceptar").addEventListener("click", function () {
        enviarAccion("/aprobar-formulario");
    });

    document.getElementById("btnRechazar").addEventListener("click", function () {
        enviarAccion("/rechazar-formulario");
    });
});

function cerrarModal() {
    document.getElementById('detalleModal').classList.add('hidden');
}