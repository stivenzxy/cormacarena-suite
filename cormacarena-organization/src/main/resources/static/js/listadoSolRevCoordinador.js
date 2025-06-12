// Variables globales
let codigoSolicitudActual = null
let estadoSolicitudActual = null

// Inicialización cuando el DOM está listo
document.addEventListener("DOMContentLoaded", () => {
    inicializarSidebar()
    inicializarAnimaciones()
    inicializarEventListeners()
})

// Funciones de inicialización
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

function inicializarEventListeners() {
    document.getElementById("btnAceptar").addEventListener("click", aprobarSolicitud)
    document.getElementById("btnRechazar").addEventListener("click", rechazarSolicitud)
}

// Función para mostrar/ocultar secciones según el estado
function manejarVisibilidadSecciones(estado) {
    const seccionObservaciones = document.getElementById("seccionObservaciones")
    const seccionLicenciaPagada = document.getElementById("seccionLicenciaPagada")

    // Ocultar observaciones si el estado es "Solicitud Pagada" o "Licencia Pagada"
    if (estado === "Solicitud Pagada" || estado === "Licencia Pagada") {
        seccionObservaciones.classList.add("hidden")
    } else {
        seccionObservaciones.classList.remove("hidden")
    }

    // Mostrar información adicional solo si el estado es "Licencia Pagada"
    if (estado === "Licencia Pagada") {
        seccionLicenciaPagada.classList.remove("hidden")
    } else {
        seccionLicenciaPagada.classList.add("hidden")
    }
}

// Funciones del modal
async function cargarDetalle(buttonElement) {
    const codigoSolicitud = buttonElement.dataset.codigo
    const estado = buttonElement.dataset.estado

    const modal = document.getElementById("detalleModal")
    const modalContent = document.getElementById("modalContent")
    const btnAceptar = document.getElementById("btnAceptar")
    const btnRechazar = document.getElementById("btnRechazar")

    // Guardar estado actual
    codigoSolicitudActual = codigoSolicitud
    estadoSolicitudActual = estado

    // Manejar visibilidad de secciones según el estado
    manejarVisibilidadSecciones(estado)

    // Mostrar/ocultar botones según el estado
    if (estado && estado.toUpperCase() === "ENVIADO") {
        btnAceptar.classList.remove("hidden")
        btnRechazar.classList.remove("hidden")
    } else {
        btnAceptar.classList.add("hidden")
        btnRechazar.classList.add("hidden")
    }

    modal.classList.remove("hidden")

    modalContent.innerHTML = `
        <div class="flex items-center justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            <span class="ml-3 text-gray-600">Cargando información...</span>
        </div>
    `

    try {
        const response = await fetch(`coordinador-grupo/evaluacion-solicitud/${codigoSolicitud}`)

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

        // Si el estado es "Licencia Pagada", llenar los campos adicionales
        if (estado === "Licencia Pagada") {
            document.getElementById("profesionalAsignadoDisplay").value = data.profesionalAsignado || "N/A"
            document.getElementById("fechaConceptoTecnicoDisplay").value = data.fechaConceptoTecnico || ""
            document.getElementById("observacionesVisitaTecnicaDisplay").value = data.observacionesVisitaTecnica || "N/A"
        }
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
    estadoSolicitudActual = null
}

// Funciones de validación
function observacionFaltanteAlert() {
    Swal.fire({
        icon: "error",
        title: "Error",
        text: "Debe ingresar alguna observación para el solicitante",
    })
}

// Funciones de aprobación y rechazo (solo para estado "ENVIADO")
async function aprobarSolicitud() {
    const observacionElement = document.getElementById("observacionInput")
    let observacion = observacionElement ? observacionElement.value.trim() : ""
    observacion = observacion.replace(/[\r\n]+/g, " ")

    if (!codigoSolicitudActual) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se ha seleccionado ninguna solicitud",
        })
        return
    } else if (observacion === "") {
        observacionFaltanteAlert()
        return
    }

    const result = await Swal.fire({
        title: "¿Aprobar solicitud?",
        text: `¿Está seguro de que desea aprobar la solicitud ${codigoSolicitudActual}?`,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#16a34a",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, aprobar",
        cancelButtonText: "Cancelar",
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Aprobando solicitud",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("processId", codigoSolicitudActual)
        formData.append("observacion", observacion)

        const response = await fetch("/coordinador-grupo/aprobacion-formulario", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: mensaje,
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
        console.error("Error al aprobar:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al aprobar la solicitud",
            confirmButtonColor: "#dc2626",
        })
    }
}

async function rechazarSolicitud() {
    const observacionElement = document.getElementById("observacionInput")
    let observacion = observacionElement ? observacionElement.value.trim() : ""
    observacion = observacion.replace(/[\r\n]+/g, " ")

    if (!codigoSolicitudActual) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se ha seleccionado ninguna solicitud",
        })
        return
    } else if (observacion === "") {
        observacionFaltanteAlert()
        return
    }

    const result = await Swal.fire({
        title: "¿Rechazar solicitud?",
        text: `¿Está seguro de que desea rechazar la solicitud ${codigoSolicitudActual}?`,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#dc2626",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, rechazar",
        cancelButtonText: "Cancelar",
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Rechazando solicitud",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("processId", codigoSolicitudActual)
        formData.append("observacion", observacion)

        const response = await fetch("/coordinador-grupo/rechazo-formulario", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: mensaje,
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
        console.error("Error al rechazar:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al rechazar la solicitud",
            confirmButtonColor: "#dc2626",
        })
    }
}

async function asignarProfesional(codigoSolicitud) {
    const { value: nombreProfesional } = await Swal.fire({
        title: "Asignar Profesional",
        text: "Ingrese el nombre del profesional a asignar:",
        input: "text",
        inputPlaceholder: "Nombre completo del profesional",
        showCancelButton: true,
        confirmButtonText: "Asignar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#8b5cf6",
        cancelButtonColor: "#6b7280",
        inputValidator: (value) => {
            if (!value || value.trim() === "") {
                return "Debe ingresar el nombre del profesional"
            }
            if (value.trim().length < 3) {
                return "El nombre debe tener al menos 3 caracteres"
            }
        },
    })

    if (nombreProfesional) {
        try {
            Swal.fire({
                title: "Procesando...",
                text: "Asignando profesional",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading()
                },
            })

            const formData = new FormData()
            formData.append("codigoSolicitud", codigoSolicitud)
            formData.append("nombreProfesional", nombreProfesional.trim())

            const response = await fetch("/coordinador-grupo/asignar-profesional", {
                method: "POST",
                body: formData,
            })

            if (response.ok) {
                const mensaje = await response.text()

                Swal.fire({
                    icon: "success",
                    title: "¡Éxito!",
                    text: mensaje || `Profesional ${nombreProfesional} asignado correctamente`,
                    confirmButtonColor: "#8b5cf6",
                }).then(() => {
                    window.location.reload()
                })
            } else {
                const error = await response.text()
                throw new Error(error)
            }
        } catch (error) {
            console.error("Error al asignar profesional:", error)
            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message || "Error al asignar el profesional",
                confirmButtonColor: "#dc2626",
            })
        }
    }
}

// Función para confirmar acto administrativo (solo para estado "Licencia Pagada")
async function confirmarActoAdministrativo(codigoSolicitud) {
    const result = await Swal.fire({
        title: "¿Confirmar Acto Administrativo?",
        text: `¿Está seguro de que desea confirmar el acto administrativo para la solicitud ${codigoSolicitud}?`,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#059669",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, confirmar",
        cancelButtonText: "Cancelar",
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Confirmando acto administrativo",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("codigoSolicitud", codigoSolicitud)

        const response = await fetch("/coordinador-grupo/aprobar-concepto-tecnico", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: mensaje || "Acto administrativo confirmado correctamente",
                confirmButtonColor: "#059669",
            }).then(() => {
                window.location.reload()
            })
        } else {
            const error = await response.text()
            throw new Error(error)
        }
    } catch (error) {
        console.error("Error al confirmar acto administrativo:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al confirmar el acto administrativo",
            confirmButtonColor: "#dc2626",
        })
    }
}
