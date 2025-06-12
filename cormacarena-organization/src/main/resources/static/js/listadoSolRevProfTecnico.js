let codigoSolicitudActual = null

document.addEventListener("DOMContentLoaded", () => {
    inicializarSidebar()
    inicializarAnimaciones()
    inicializarEventListeners()
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

function inicializarEventListeners() {
    const visitaCheckbox = document.getElementById("visitaRealizada")
    const camposVisita = document.getElementById("camposVisita")

    if (visitaCheckbox && camposVisita) {
        visitaCheckbox.addEventListener("change", function () {
            if (this.checked) {
                camposVisita.classList.remove("opacity-50", "pointer-events-none")
                camposVisita.classList.add("opacity-100")
            } else {
                camposVisita.classList.add("opacity-50", "pointer-events-none")
                camposVisita.classList.remove("opacity-100")
                limpiarCamposVisita()
            }
        })
    }
}

function limpiarCamposVisita() {
    document.getElementById("fechaVisitaTecnica").value = ""
    document.getElementById("coherenciaImpactoEIA").checked = false
    document.getElementById("observacionesVisitaTecnica").value = ""
}

function abrirModalVisita(buttonElement) {
    const codigoSolicitud = buttonElement.dataset.codigo
    const profesionalAsignado = buttonElement.dataset.profesional

    document.getElementById("visitaForm").reset()
    limpiarCamposVisita()

    codigoSolicitudActual = codigoSolicitud

    document.getElementById("profesionalAsignado").value = profesionalAsignado || "No asignado"

    const camposVisita = document.getElementById("camposVisita")
    camposVisita.classList.add("opacity-50", "pointer-events-none")
    camposVisita.classList.remove("opacity-100")

    document.getElementById("visitaModal").classList.remove("hidden")
}

function abrirModalConcepto(buttonElement) {
    const codigoSolicitud = buttonElement.dataset.codigo
    const profesionalAsignado = buttonElement.dataset.profesional
    const fechaVisita = buttonElement.dataset.fechaVisita
    const observacionesVisita = buttonElement.dataset.observacionesVisita

    codigoSolicitudActual = codigoSolicitud

    document.getElementById("fechaVisitaTecnicaConcepto").value = fechaVisita || ""
    document.getElementById("profesionalAsignadoConcepto").value = profesionalAsignado || "No asignado"
    document.getElementById("observacionesVisitaTecnicaConcepto").value = observacionesVisita || ""
    document.getElementById("fechaConceptoTecnico").value = ""

    document.getElementById("conceptoModal").classList.remove("hidden")
}

function cerrarModal() {
    document.getElementById("visitaModal").classList.add("hidden")
    codigoSolicitudActual = null
}

function cerrarModalConcepto() {
    document.getElementById("conceptoModal").classList.add("hidden")
    codigoSolicitudActual = null
}

async function registrarVisita() {
    if (!codigoSolicitudActual) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se ha seleccionado ninguna solicitud",
        })
        return
    }

    const visitaRealizada = document.getElementById("visitaRealizada").checked
    const fechaVisita = document.getElementById("fechaVisitaTecnica").value
    const profesionalAsignado = document.getElementById("profesionalAsignado").value
    const coherenciaEIA = document.getElementById("coherenciaImpactoEIA").checked
    const observaciones = document.getElementById("observacionesVisitaTecnica").value.trim()

    const textoConfirmacion = visitaRealizada
        ? "¿Está seguro de que desea registrar esta visita técnica?"
        : "¿Está seguro de que desea registrar que NO se realizó la visita técnica?"

    const result = await Swal.fire({
        title: "Confirmar registro",
        text: textoConfirmacion,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#3b82f6",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, registrar",
        cancelButtonText: "Cancelar",
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Registrando visita técnica",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("codigoSolicitud", codigoSolicitudActual)
        formData.append("visita", visitaRealizada)

        if (visitaRealizada) {
            formData.append("fechaVisitaTecnica", fechaVisita)
            formData.append("profesionalAsignado", profesionalAsignado)
            formData.append("coherenciaImpactoEIA", coherenciaEIA)
            formData.append("observacionesVisitaTecnica", observaciones)
        }

        const response = await fetch("/profesional-tecnico/registrar-visita", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: mensaje || "Visita técnica registrada correctamente",
                confirmButtonColor: "#3b82f6",
            }).then(() => {
                cerrarModal()
                window.location.reload()
            })
        } else {
            const error = await response.text()
            throw new Error(error)
        }
    } catch (error) {
        console.error("Error al registrar visita:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al registrar la visita técnica",
            confirmButtonColor: "#dc2626",
        })
    }
}

async function elaborarConcepto() {
    if (!codigoSolicitudActual) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "No se ha seleccionado ninguna solicitud",
        })
        return
    }

    const fechaConcepto = document.getElementById("fechaConceptoTecnico").value

    if (!fechaConcepto) {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: "Debe ingresar la fecha del concepto técnico",
        })
        return
    }

    const result = await Swal.fire({
        title: "¿Elaborar concepto técnico?",
        text: `¿Está seguro de que desea elaborar el concepto técnico para la solicitud ${codigoSolicitudActual}?`,
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#059669",
        cancelButtonColor: "#6b7280",
        confirmButtonText: "Sí, elaborar",
        cancelButtonText: "Cancelar",
    })

    if (!result.isConfirmed) return

    try {
        Swal.fire({
            title: "Procesando...",
            text: "Elaborando concepto técnico",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading()
            },
        })

        const formData = new FormData()
        formData.append("codigoSolicitud", codigoSolicitudActual)
        formData.append("fechaConceptoTecnico", fechaConcepto)

        const response = await fetch("/profesional-tecnico/elaborar-concepto", {
            method: "POST",
            body: formData,
        })

        if (response.ok) {
            const mensaje = await response.text()

            Swal.fire({
                icon: "success",
                title: "¡Éxito!",
                text: mensaje || "Concepto técnico elaborado correctamente",
                confirmButtonColor: "#059669",
            }).then(() => {
                cerrarModalConcepto()
                window.location.reload()
            })
        } else {
            const error = await response.text()
            throw new Error(error)
        }
    } catch (error) {
        console.error("Error al elaborar concepto:", error)
        Swal.fire({
            icon: "error",
            title: "Error",
            text: error.message || "Error al elaborar el concepto técnico",
            confirmButtonColor: "#dc2626",
        })
    }
}