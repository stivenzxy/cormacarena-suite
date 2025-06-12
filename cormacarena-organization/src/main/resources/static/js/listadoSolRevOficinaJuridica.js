let codigoSolicitudActual = null

document.addEventListener("DOMContentLoaded", () => {
    inicializarSidebar()
    inicializarAnimaciones()
})

function inicializarSidebar() {
    function activarEnlace(enlaceActivo) {
        const todosLosEnlaces = document.querySelectorAll("aside nav a")

        todosLosEnlaces.forEach((enlace) => {
            enlace.className =
                "flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 text-blue-100 hover:bg-primary-hover hover:text-white group"

            const icono = enlace.querySelector("svg")
            if (icono) {
                icono.className = "h-5 w-5 text-blue-200 group-hover:text-white"
            }

            const flecha = enlace.querySelector("svg:last-child")
            if (flecha && flecha.classList.contains("ml-auto")) {
                flecha.remove()
            }
        })

        enlaceActivo.className =
            "flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200 bg-primary-hover text-white shadow-md"

        const iconoActivo = enlaceActivo.querySelector("svg")
        if (iconoActivo) {
            iconoActivo.className = "h-5 w-5 text-white"
        }

        const flecha = document.createElement("svg")
        flecha.setAttribute("xmlns", "http://www.w3.org/2000/svg")
        flecha.className = "h-4 w-4 ml-auto text-white"
        flecha.setAttribute("fill", "none")
        flecha.setAttribute("viewBox", "0 0 24 24")
        flecha.setAttribute("stroke", "currentColor")
        flecha.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />'
        enlaceActivo.appendChild(flecha)
    }

    const enlacesSidebar = document.querySelectorAll("aside nav a")
    enlacesSidebar.forEach((enlace) => {
        enlace.addEventListener("click", function () {
            activarEnlace(this)
        })
    })

    const rutaActual = window.location.pathname
    enlacesSidebar.forEach((enlace) => {
        if (enlace.getAttribute("href") === rutaActual) {
            activarEnlace(enlace)
        }
    })
}

function inicializarAnimaciones() {
    const tableRows = document.querySelectorAll(".table-row")
    tableRows.forEach((row, index) => {
        row.style.animationDelay = `${index * 0.05}s`
        row.classList.add("fade-in-up")
    })
}

async function cargarDetalle(buttonElement) {
    const codigoSolicitud = buttonElement.dataset.codigo
    const modal = document.getElementById("detalleModal")
    const modalContent = document.getElementById("modalContent")

    codigoSolicitudActual = codigoSolicitud

    document.getElementById("fechaResolucion").value = ""
    document.getElementById("descripcionJuridica").value = ""

    modal.classList.remove("hidden")

    modalContent.innerHTML = `
        <div class="flex items-center justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            <span class="ml-3 text-gray-600">Cargando información...</span>
        </div>
    `

    try {
        const response = await fetch(`asesoria-juridica/detalle-solicitud/${codigoSolicitud}`)

        if (!response.ok) {
            throw new Error("Error al cargar los datos")
        }

        const data = await response.json()

        modalContent.innerHTML = `
            <div class="grid md:grid-cols-2 gap-6 text-sm text-gray-700">
                <div class="space-y-3">
                    <h4 class="text-base font-semibold text-primary mb-3 border-b border-primary pb-2 flex items-center">
                        <i class="fa-solid fa-user mr-2"></i>
                        Información del Solicitante
                    </h4>
                    <div class="space-y-2">
                        <p><span class="font-medium text-gray-900">Nombre:</span> <span class="text-gray-700">${data.nombreSolicitante || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Tipo de Identificación:</span> <span class="text-gray-700">${data.tipoIdentificacion || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">ID del Solicitante:</span> <span class="text-gray-700">${data.idSolicitante || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Teléfono:</span> <span class="text-gray-700">${data.telefono || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Email:</span> <span class="text-gray-700">${data.email || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Dirección:</span> <span class="text-gray-700">${data.direccionResidencia || "N/A"}</span></p>
                    </div>
                </div>
                <div class="space-y-3">
                    <h4 class="text-base font-semibold text-primary mb-3 border-b border-primary pb-2 flex items-center">
                        <i class="fa-solid fa-building mr-2"></i>
                        Información del Proyecto
                    </h4>
                    <div class="space-y-2">
                        <p><span class="font-medium text-gray-900">Código:</span> <span class="text-gray-700 font-mono bg-gray-100 px-2 py-1 rounded">${data.codigoSolicitud || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Nombre del Proyecto:</span> <span class="text-gray-700">${data.nombreProyecto || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Sector:</span> <span class="text-gray-700">${data.sectorProyecto || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Valor:</span> <span class="text-gray-700">$${data.valorProyecto ? data.valorProyecto.toLocaleString("es-CO") : "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Departamento:</span> <span class="text-gray-700">${data.departamentoProyecto || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Municipio:</span> <span class="text-gray-700">${data.municipioProyecto || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Estado:</span>
                            <span class="status-badge status-${(data.estado || "pendiente").toLowerCase().replace(" ", "-")}">
                                ${data.estado || "Pendiente"}
                            </span>
                        </p>
                        <p><span class="font-medium text-gray-900">Soporte adjunto:</span>
                            <span class="text-gray-700">
                                ${
            data.nombreSoporteEIA
                ? `<i class="fa-solid fa-file-pdf text-red-500 mr-1"></i>${data.nombreSoporteEIA}`
                : "No adjunto"
        }
                            </span>
                        </p>
                    </div>
                </div>
            </div>
        `
    } catch (error) {
        console.error("Error:", error)
        modalContent.innerHTML = `
            <div class="text-center py-8">
                <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                    <i class="fa-solid fa-exclamation-triangle text-2xl text-red-500"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-700 mb-2">Error al cargar los datos</h3>
                <p class="text-gray-500">No se pudo obtener la información de la solicitud. Por favor, intente nuevamente.</p>
            </div>
        `
    }
}

function cerrarModal() {
    document.getElementById("detalleModal").classList.add("hidden")
    codigoSolicitudActual = null
}

function validarCampos() {
    const fechaResolucion = document.getElementById("fechaResolucion").value.trim()
    const descripcionJuridica = document.getElementById("descripcionJuridica").value.trim()

    if (!fechaResolucion) {
        Swal.fire({
            icon: "error",
            title: "Campo obligatorio",
            text: "Debe ingresar la fecha de resolución",
        })
        return false
    }

    if (!descripcionJuridica) {
        Swal.fire({
            icon: "error",
            title: "Campo obligatorio",
            text: "Debe ingresar la descripción jurídica de la solicitud",
        })
        return false
    }

    if (descripcionJuridica.length < 10) {
        Swal.fire({
            icon: "error",
            title: "Descripción muy corta",
            text: "La descripción jurídica debe tener al menos 10 caracteres",
        })
        return false
    }

    return true
}

async function emitirResolucion() {
    if (!codigoSolicitudActual) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se ha seleccionado ninguna solicitud",
        })
        return
    }

    if (!validarCampos()) {
        return
    }

    const fechaResolucion = document.getElementById("fechaResolucion").value
    const descripcionJuridica = document.getElementById("descripcionJuridica").value.trim()

    const result = await Swal.fire({
        title: "¿Emitir Resolución Administrativa?",
        html: `<p><strong>Código de solicitud:</strong> ${codigoSolicitudActual}</p>
           <p><strong>Fecha de resolución:</strong> ${fechaResolucion}</p>
           <p class="text-sm text-gray-600 mt-2">Esta acción enviará la resolución a la dirección general.</p>`,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#16a34a",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, emitir resolución",
        cancelButtonText: "Cancelar",
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Emitiendo resolución administrativa",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("codigoSolicitud", codigoSolicitudActual)
        formData.append("fechaResolucion", fechaResolucion)
        formData.append("descripcionJuridica", descripcionJuridica)

        const response = await fetch("/asesoria-juridica/emitir-resolucion-administrativa", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: mensaje || "Resolución administrativa emitida correctamente",
                confirmButtonColor: "#16a34a",
            }).then(() => {
                cerrarModal()
                window.location.reload()
            })
        } else {
            const error = await response.text()
            throw new Error(error)
        }
    } catch (error) {
        console.error("Error al emitir resolución:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al emitir la resolución administrativa",
            confirmButtonColor: "#dc2626",
        })
    }
}
