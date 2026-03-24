document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const sidebarToggle = document.getElementById('sidebar-toggle');
    const themeToggle = document.getElementById('theme-toggle');
    const container = document.querySelector('.app-container');
    const html = document.documentElement;

    // Sidebar Toggle
    sidebarToggle.addEventListener('click', () => {
        container.classList.toggle('collapsed');
        // Smoothly adjust Lucide icons if needed
        if (typeof lucide !== 'undefined') {
            lucide.createIcons();
        }
    });

    // Theme Toggle
    const currentTheme = localStorage.getItem('theme') || 'light';
    html.setAttribute('data-theme', currentTheme);

    themeToggle.addEventListener('click', () => {
        const theme = html.getAttribute('data-theme');
        const newTheme = theme === 'light' ? 'dark' : 'light';
        
        html.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        
        // Re-calculate some styles if necessary
    });

    // Handle Responsive Sidebar
    const handleResize = () => {
        if (window.innerWidth < 1024) {
            container.classList.add('collapsed');
        } else {
            container.classList.remove('collapsed');
        }
    };

    window.addEventListener('resize', handleResize);
    handleResize(); // Initial check
});
