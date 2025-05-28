/**
 * PC Maker Admin Panel JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    
    // Обработка подтверждений перед удалением
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('Вы уверены, что хотите удалить этот элемент?')) {
                e.preventDefault();
                return false;
            }
        });
    });
    
    // Инициализация выпадающих меню
    const dropdownToggle = document.querySelectorAll('.dropdown-toggle');
    dropdownToggle.forEach(dropdown => {
        dropdown.addEventListener('click', function(e) {
            e.preventDefault();
            const dropdownMenu = this.nextElementSibling;
            if (dropdownMenu.classList.contains('show')) {
                dropdownMenu.classList.remove('show');
            } else {
                // Закрыть все открытые меню
                document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                    menu.classList.remove('show');
                });
                dropdownMenu.classList.add('show');
            }
        });
    });
    
    // Закрытие меню при клике вне его
    document.addEventListener('click', function(e) {
        if (!e.target.matches('.dropdown-toggle') && !e.target.closest('.dropdown-menu')) {
            document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
    
    // Мобильное меню - переключение сайдбара
    const sidebarToggle = document.querySelector('.navbar-toggler');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.querySelector('#sidebar').classList.toggle('show');
        });
    }
    
    // Автоматическое скрытие уведомлений через 5 секунд
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const closeButton = alert.querySelector('.btn-close');
            if (closeButton) {
                closeButton.click();
            }
        }, 5000);
    });
    
    // Инициализация тултипов, если они используются
    const tooltips = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    tooltips.forEach(tooltip => {
        new bootstrap.Tooltip(tooltip);
    });
    
    // Форматирование денежных значений
    const moneyElements = document.querySelectorAll('.money-format');
    moneyElements.forEach(element => {
        const value = parseFloat(element.textContent);
        if (!isNaN(value)) {
            element.textContent = new Intl.NumberFormat('ru-RU', {
                style: 'currency',
                currency: 'RUB',
                minimumFractionDigits: 2
            }).format(value);
        }
    });
    
    // Обработка форм с загрузкой файлов
    const fileInputs = document.querySelectorAll('input[type="file"]');
    fileInputs.forEach(input => {
        input.addEventListener('change', function() {
            const fileName = this.files[0] ? this.files[0].name : 'Выберите файл';
            const fileLabel = this.nextElementSibling;
            if (fileLabel && fileLabel.classList.contains('custom-file-label')) {
                fileLabel.textContent = fileName;
            }
        });
    });
}); 