// Variables globales
let codigoSolicitudActual = null

// Inicialización cuando el DOM está listo
document.addEventListener("DOMContentLoaded", () => {
    inicializarSidebar()
    inicializarAnimaciones()
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

// Funciones del modal
async function cargarDetalle(buttonElement) {
    const codigoSolicitud = buttonElement.dataset.codigo
    const modal = document.getElementById("detalleModal")
    const modalContent = document.getElementById("modalContent")

    // Guardar código actual
    codigoSolicitudActual = codigoSolicitud

    modal.classList.remove("hidden")

    modalContent.innerHTML = `
        <div class="flex items-center justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
            <span class="ml-3 text-gray-600">Cargando información...</span>
        </div>
    `

    try {
        const response = await fetch(`direccion-general/detalle-solicitud/${codigoSolicitud}`)

        if (!response.ok) {
            throw new Error("Error al cargar los datos")
        }

        const data = await response.json()

        // Mostrar información básica de la solicitud
        modalContent.innerHTML = `
            <div class="grid md:grid-cols-2 gap-6 text-sm text-gray-700">
                <div class="space-y-3">
                    <h4 class="text-base font-semibold text-primary mb-3 border-b border-primary pb-2 flex items-center">
                        <i class="fa-solid fa-info-circle mr-2"></i>
                        Información de la Solicitud
                    </h4>
                    <div class="space-y-2">
                        <p><span class="font-medium text-gray-900">Código de Solicitud:</span> 
                           <span class="text-gray-700 font-mono bg-gray-100 px-2 py-1 rounded">${data.codigoSolicitud || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Estado:</span>
                            <span class="status-badge status-${(data.estado || "pendiente").toLowerCase().replace(" ", "-")}">
                                ${data.estado || "Pendiente"}
                            </span>
                        </p>
                        <p><span class="font-medium text-gray-900">Nombre del Proyecto:</span> <span class="text-gray-700">${data.nombreProyecto || "N/A"}</span></p>
                        <p><span class="font-medium text-gray-900">Solicitante:</span> <span class="text-gray-700">${data.nombreSolicitante || "N/A"}</span></p>
                    </div>
                </div>
                <div class="space-y-3">
                    <h4 class="text-base font-semibold text-primary mb-3 border-b border-primary pb-2 flex items-center">
                        <i class="fa-solid fa-chart-line mr-2"></i>
                        Estado del Proceso
                    </h4>
                    <div class="bg-green-50 border border-green-200 rounded-lg p-4">
                        <div class="flex items-center">
                            <i class="fa-solid fa-check-circle text-green-600 text-xl mr-3"></i>
                            <div>
                                <p class="font-medium text-green-800">Proceso Completado</p>
                                <p class="text-sm text-green-600">La solicitud ha pasado por todas las etapas de evaluación</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `

        // Llenar los campos de resolución jurídica
        document.getElementById("fechaResolucionJuridica").value = data.fechaResolucionJuridica || ""
        document.getElementById("descripcionResolucionJuridica").value = data.descripcionResolucionJuridica || "N/A"
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

// Función para firmar y autorizar
async function firmarYAutorizar() {
    if (!codigoSolicitudActual) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se ha seleccionado ninguna solicitud",
        })
        return
    }

    const fechaResolucion = document.getElementById("fechaResolucionJuridica").value
    const descripcionResolucion = document.getElementById("descripcionResolucionJuridica").value

    const result = await Swal.fire({
        title: "¿Firmar y Autorizar Licencia?",
        html: `<div class="text-left">
             <p><strong>Código de solicitud:</strong> ${codigoSolicitudActual}</p>
             <p><strong>Fecha de resolución jurídica:</strong> ${fechaResolucion || "N/A"}</p>
             <div class="mt-3 p-3 bg-yellow-50 border border-yellow-200 rounded">
               <p class="text-sm text-yellow-800"><strong>⚠️ Importante:</strong></p>
               <p class="text-sm text-yellow-700">Esta acción otorgará oficialmente la licencia ambiental y no podrá ser revertida.</p>
             </div>
           </div>`,
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#16a34a",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, firmar y autorizar",
        cancelButtonText: "Cancelar",
        customClass: {
            popup: "swal-wide",
        },
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Firmando y autorizando licencia ambiental",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("codigoSolicitud", codigoSolicitudActual)

        const response = await fetch("/direccion-general/otorgar-licencia", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Licencia Otorgada!",
                html: `<div class="text-center">
                 <p class="mb-3">${mensaje || "Licencia ambiental otorgada exitosamente"}</p>
                 <div class="bg-green-50 border border-green-200 rounded-lg p-3">
                   <p class="text-sm text-green-700">
                     <i class="fa-solid fa-certificate mr-2"></i>
                     La licencia ambiental ha sido oficialmente autorizada
                   </p>
                 </div>
               </div>`,
                confirmButtonColor: "#16a34a",
                confirmButtonText: "Entendido",
            }).then(() => {
                cerrarModal()
                window.location.reload()
            })
        } else {
            const error = await response.text()
            throw new Error(error)
        }
    } catch (error) {
        console.error("Error al otorgar licencia:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al otorgar la licencia ambiental",
            confirmButtonColor: "#dc2626",
        })
    }
}
