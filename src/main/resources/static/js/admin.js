/**
 * Общие функции для админ-панели
 */
document.addEventListener('DOMContentLoaded', function() {
    // Инициализация всплывающих подсказок
    initTooltips();
    
    // Инициализация модальных окон подтверждения действий
    initConfirmActions();
    
    // Инициализация предпросмотра изображений
    initImagePreview();
    
    // Инициализация автоматического скрытия алертов
    initAutoHideAlerts();
});

/**
 * Инициализация всплывающих подсказок Bootstrap
 */
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Инициализация модальных окон подтверждения действий
 */
function initConfirmActions() {
    const confirmButtons = document.querySelectorAll('[data-confirm]');
    
    confirmButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            
            const message = this.getAttribute('data-confirm') || 'Вы уверены, что хотите выполнить это действие?';
            const form = this.closest('form');
            
            if (confirm(message)) {
                if (form) {
                    form.submit();
                } else if (this.tagName === 'A') {
                    window.location.href = this.getAttribute('href');
                }
            }
        });
    });
}

/**
 * Инициализация предпросмотра изображений
 */
function initImagePreview() {
    const imageInputs = document.querySelectorAll('input[type="file"][data-preview]');
    
    imageInputs.forEach(input => {
        const previewId = input.getAttribute('data-preview');
        const previewElement = document.getElementById(previewId);
        
        if (previewElement) {
            input.addEventListener('change', function() {
                if (this.files && this.files[0]) {
                    const reader = new FileReader();
                    
                    reader.onload = function(e) {
                        previewElement.src = e.target.result;
                        previewElement.style.display = 'block';
                    };
                    
                    reader.readAsDataURL(this.files[0]);
                }
            });
        }
    });
}

/**
 * Инициализация автоматического скрытия алертов
 */
function initAutoHideAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });
}

/**
 * Функция для динамического добавления полей формы
 * @param {string} containerId ID контейнера, куда добавлять поля
 * @param {string} templateId ID шаблона поля
 */
function addFormField(containerId, templateId) {
    const container = document.getElementById(containerId);
    const template = document.getElementById(templateId);
    
    if (container && template) {
        const clone = template.content.cloneNode(true);
        const newFieldIndex = container.children.length;
        
        // Обновляем индексы в именах полей
        const inputs = clone.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            const name = input.getAttribute('name');
            if (name) {
                input.setAttribute('name', name.replace(/\[\d+\]/, `[${newFieldIndex}]`));
            }
        });
        
        container.appendChild(clone);
    }
}

/**
 * Функция для удаления поля формы
 * @param {Element} button Кнопка удаления
 */
function removeFormField(button) {
    const fieldGroup = button.closest('.form-group');
    if (fieldGroup) {
        fieldGroup.remove();
    }
} 