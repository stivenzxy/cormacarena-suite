// Variables globales para el sidebar
let sidebarOpen = false;
let isInitialized = false;

// Función principal para alternar el sidebar
function toggleSidebar() {
    console.log('Toggle sidebar called, current state:', sidebarOpen);

    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("overlay");
    const menuIcon = document.getElementById("menuIcon");
    const mainContent = document.getElementById("mainContent");

    // Verificar que los elementos existan
    if (!sidebar) {
        console.error('Sidebar element not found');
        return;
    }

    sidebarOpen = !sidebarOpen;
    console.log('New sidebar state:', sidebarOpen);

    if (sidebarOpen) {
        // Abrir sidebar
        sidebar.classList.remove("-translate-x-full");
        sidebar.classList.add("translate-x-0");

        if (overlay) {
            overlay.classList.remove("hidden");
            overlay.classList.add("opacity-100");
        }

        if (menuIcon) {
            menuIcon.classList.remove("fa-bars");
            menuIcon.classList.add("fa-times");
        }

        // Ajustar contenido principal en pantallas grandes
        if (mainContent && window.innerWidth >= 1024) {
            mainContent.style.marginLeft = "320px";
            mainContent.style.transition = "margin-left 0.3s ease-in-out";
        }

        // Prevenir scroll del body
        document.body.style.overflow = 'hidden';

    } else {
        // Cerrar sidebar
        sidebar.classList.add("-translate-x-full");
        sidebar.classList.remove("translate-x-0");

        if (overlay) {
            overlay.classList.add("hidden");
            overlay.classList.remove("opacity-100");
        }

        if (menuIcon) {
            menuIcon.classList.remove("fa-times");
            menuIcon.classList.add("fa-bars");
        }

        // Restaurar contenido principal
        if (mainContent) {
            mainContent.style.marginLeft = "";
        }

        // Restaurar scroll del body
        document.body.style.overflow = '';
    }
}

// Función para cerrar sidebar al hacer clic fuera
function closeSidebarOnClickOutside(e) {
    if (!sidebarOpen) return;

    const sidebar = document.getElementById("sidebar");
    const sidebarToggle = document.getElementById("sidebarToggle");

    // Si el clic fue fuera del sidebar y no en el botón toggle
    if (sidebar && !sidebar.contains(e.target) &&
        sidebarToggle && !sidebarToggle.contains(e.target)) {
        toggleSidebar();
    }
}

// Función para cerrar sidebar con overlay
function closeSidebarWithOverlay() {
    if (sidebarOpen) {
        toggleSidebar();
    }
}

// Función para manejar el redimensionamiento de ventana
function handleResize() {
    const mainContent = document.getElementById("mainContent");
    const overlay = document.getElementById("overlay");

    if (window.innerWidth >= 1024) {
        // En pantallas grandes, ocultar overlay
        if (overlay) {
            overlay.classList.add("hidden");
        }

        // Ajustar margen del contenido principal
        if (mainContent && sidebarOpen) {
            mainContent.style.marginLeft = "320px";
        }
    } else {
        // En pantallas pequeñas, mostrar overlay si sidebar está abierto
        if (overlay && sidebarOpen) {
            overlay.classList.remove("hidden");
        }

        // Remover margen en móviles
        if (mainContent) {
            mainContent.style.marginLeft = "";
        }
    }
}

// Función para manejar el scroll del header
function handleHeaderScroll() {
    let prevScroll = window.scrollY;
    const header = document.getElementById("mainHeader");

    if (!header) return;

    window.addEventListener("scroll", () => {
        const currentScroll = window.scrollY;

        if (currentScroll > prevScroll && currentScroll > 100) {
            // Scroll hacia abajo - ocultar header
            header.style.transform = "translateY(-100%)";
        } else {
            // Scroll hacia arriba - mostrar header
            header.style.transform = "translateY(0)";
        }

        prevScroll = currentScroll;
    });
}

// Función para manejar teclas
function handleKeyPress(e) {
    if (e.key === 'Escape' && sidebarOpen) {
        toggleSidebar();
    }
}

// Función para inicializar animaciones del sidebar
function initSidebarAnimations() {
    const sidebarItems = document.querySelectorAll('#sidebar nav a');
    sidebarItems.forEach((item, index) => {
        if (item) {
            item.style.animationDelay = `${index * 0.1}s`;
            item.classList.add('animate-fade-in');
        }
    });
}

// Función principal de inicialización
function initSidebar() {
    if (isInitialized) {
        console.log('Sidebar already initialized');
        return;
    }

    console.log('Initializing sidebar...');

    // Verificar que los elementos principales existan
    const sidebar = document.getElementById("sidebar");
    const sidebarToggle = document.getElementById("sidebarToggle");
    const overlay = document.getElementById("overlay");

    if (!sidebar) {
        console.error('Sidebar element not found, retrying in 100ms...');
        setTimeout(initSidebar, 100);
        return;
    }

    console.log('Sidebar elements found, setting up event listeners...');

    // Configurar event listeners
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            toggleSidebar();
        });
    }

    if (overlay) {
        overlay.addEventListener('click', closeSidebarWithOverlay);
    }

    // Event listeners globales
    document.addEventListener("click", closeSidebarOnClickOutside);
    document.addEventListener('keydown', handleKeyPress);
    window.addEventListener('resize', handleResize);

    // Inicializar scroll del header
    handleHeaderScroll();

    // Inicializar animaciones
    initSidebarAnimations();

    // Asegurar estado inicial correcto
    if (sidebar) {
        sidebar.classList.add("-translate-x-full");
        sidebar.classList.remove("translate-x-0");
    }

    if (overlay) {
        overlay.classList.add("hidden");
    }

    isInitialized = true;
    console.log('Sidebar initialized successfully');
}

// Función de respaldo para inicialización
function ensureSidebarInit() {
    if (!isInitialized) {
        initSidebar();
    }
}

// Múltiples puntos de inicialización para asegurar que funcione
document.addEventListener("DOMContentLoaded", initSidebar);
window.addEventListener("load", ensureSidebarInit);

// Función global para debugging
window.debugSidebar = function() {
    console.log('Sidebar state:', sidebarOpen);
    console.log('Sidebar element:', document.getElementById("sidebar"));
    console.log('Toggle button:', document.getElementById("sidebarToggle"));
    console.log('Overlay:', document.getElementById("overlay"));
    console.log('Is initialized:', isInitialized);
};