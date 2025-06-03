let sidebarOpen = false;
let isInitialized = false;
let gemaSubmenuOpen = false;
let giemaSubmenuOpen = false;
let autoridadSubmenuOpen = false; // Nueva variable para el submenú de Autoridad Ambiental

// Función para alternar el submenú de Autoridad Ambiental
function toggleAutoridadSubmenu() {
    const submenu = document.getElementById("autoridadSubmenu");
    const chevron = document.getElementById("autoridadChevron");

    if (!submenu || !chevron) return;

    autoridadSubmenuOpen = !autoridadSubmenuOpen;

    if (autoridadSubmenuOpen) {
        submenu.classList.remove("hidden");
        submenu.classList.add("animate-slide-down");
        chevron.classList.remove("fa-chevron-down");
        chevron.classList.add("fa-chevron-up");
        chevron.style.transform = "rotate(180deg)";
    } else {
        submenu.classList.add("hidden");
        submenu.classList.remove("animate-slide-down");
        chevron.classList.remove("fa-chevron-up");
        chevron.classList.add("fa-chevron-down");
        chevron.style.transform = "rotate(0deg)";
    }
}

// Función para alternar el submenú de GEMA
function toggleGemaSubmenu() {
    const submenu = document.getElementById("gemaSubmenu");
    const chevron = document.getElementById("gemaChevron");

    if (!submenu || !chevron) return;

    gemaSubmenuOpen = !gemaSubmenuOpen;

    if (gemaSubmenuOpen) {
        submenu.classList.remove("hidden");
        submenu.classList.add("animate-slide-down");
        chevron.classList.remove("fa-chevron-down");
        chevron.classList.add("fa-chevron-up");
        chevron.style.transform = "rotate(180deg)";
    } else {
        submenu.classList.add("hidden");
        submenu.classList.remove("animate-slide-down");
        chevron.classList.remove("fa-chevron-up");
        chevron.classList.add("fa-chevron-down");
        chevron.style.transform = "rotate(0deg)";
    }
}

// Función para alternar el submenú de GIEMA
function toggleGiemaSubmenu() {
    const submenu = document.getElementById("giemaSubmenu");
    const chevron = document.getElementById("giemaChevron");

    if (!submenu || !chevron) return;

    giemaSubmenuOpen = !giemaSubmenuOpen;

    if (giemaSubmenuOpen) {
        submenu.classList.remove("hidden");
        submenu.classList.add("animate-slide-down");
        chevron.classList.remove("fa-chevron-down");
        chevron.classList.add("fa-chevron-up");
        chevron.style.transform = "rotate(180deg)";
    } else {
        submenu.classList.add("hidden");
        submenu.classList.remove("animate-slide-down");
        chevron.classList.remove("fa-chevron-up");
        chevron.classList.add("fa-chevron-down");
        chevron.style.transform = "rotate(0deg)";
    }
}

// Función principal para alternar el sidebar
function toggleSidebar() {
    console.log('Toggle sidebar called, current state:', sidebarOpen);

    const sidebar = document.getElementById("sidebar");
    const overlay = document.getElementById("overlay");
    const menuIcon = document.getElementById("menuIcon");
    const mainContent = document.getElementById("mainContent");

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
        }

        if (menuIcon) {
            menuIcon.classList.remove("fa-bars");
            menuIcon.classList.add("fa-times");
        }

        // Ajustar contenido principal en pantallas grandes
        if (mainContent && window.innerWidth >= 1024) {
            mainContent.style.marginLeft = "288px";
            mainContent.style.transition = "margin-left 0.3s ease-in-out";
        }

        // Prevenir scroll del body en móviles
        if (window.innerWidth < 1024) {
            document.body.style.overflow = 'hidden';
        }

    } else {
        // Cerrar sidebar
        sidebar.classList.add("-translate-x-full");
        sidebar.classList.remove("translate-x-0");

        if (overlay) {
            overlay.classList.add("hidden");
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

        // Cerrar submenús al cerrar sidebar
        if (gemaSubmenuOpen) {
            toggleGemaSubmenu();
        }
        if (giemaSubmenuOpen) {
            toggleGiemaSubmenu();
        }
        if (autoridadSubmenuOpen) {
            toggleAutoridadSubmenu();
        }
    }
}

// Función para cerrar sidebar al hacer clic fuera
function closeSidebarOnClickOutside(e) {
    if (!sidebarOpen) return;

    const sidebar = document.getElementById("sidebar");
    const sidebarToggle = document.getElementById("sidebarToggle");

    if (sidebar && !sidebar.contains(e.target) &&
        sidebarToggle && !sidebarToggle.contains(e.target)) {
        toggleSidebar();
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

// Función para manejar el redimensionamiento de ventana
function handleResize() {
    const mainContent = document.getElementById("mainContent");
    const overlay = document.getElementById("overlay");

    if (window.innerWidth >= 1024) {
        if (overlay) {
            overlay.classList.add("hidden");
        }
        if (mainContent && sidebarOpen) {
            mainContent.style.marginLeft = "288px";
        }
        // Restaurar scroll en desktop
        document.body.style.overflow = '';
    } else {
        if (overlay && sidebarOpen) {
            overlay.classList.remove("hidden");
        }
        if (mainContent) {
            mainContent.style.marginLeft = "";
        }
        // Prevenir scroll en móvil si sidebar está abierto
        if (sidebarOpen) {
            document.body.style.overflow = 'hidden';
        }
    }
}

// Función para manejar teclas
function handleKeyPress(e) {
    if (e.key === 'Escape' && sidebarOpen) {
        toggleSidebar();
    }
}

// Función principal de inicialización
function initSidebar() {
    if (isInitialized) {
        console.log('Sidebar already initialized');
        return;
    }

    console.log('Initializing sidebar...');

    const sidebar = document.getElementById("sidebar");
    const sidebarToggle = document.getElementById("sidebarToggle");
    const overlay = document.getElementById("overlay");
    const gemaToggle = document.getElementById("gemaToggle");
    const giemaToggle = document.getElementById("giemaToggle");
    const autoridadToggle = document.getElementById("autoridadToggle"); // Nuevo toggle para Autoridad Ambiental

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
        overlay.addEventListener('click', function() {
            if (sidebarOpen) {
                toggleSidebar();
            }
        });
    }

    if (gemaToggle) {
        gemaToggle.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            toggleGemaSubmenu();
        });
    }

    if (giemaToggle) {
        giemaToggle.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            toggleGiemaSubmenu();
        });
    }

    // Nuevo event listener para Autoridad Ambiental
    if (autoridadToggle) {
        autoridadToggle.addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            toggleAutoridadSubmenu();
        });
    }

    // Event listeners globales
    document.addEventListener("click", closeSidebarOnClickOutside);
    document.addEventListener('keydown', handleKeyPress);
    window.addEventListener('resize', handleResize);

    // Inicializar scroll del header
    handleHeaderScroll();

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

// Inicializar cuando el DOM esté listo
document.addEventListener("DOMContentLoaded", initSidebar);