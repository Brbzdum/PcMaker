/**
 * PC Maker Admin Panel JavaScript
 */
document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    initTooltips();
    
    // Initialize confirm actions
    initConfirmActions();
    
    // Initialize image previews
    initImagePreview();
    
    // Initialize auto-hide alerts
    initAutoHideAlerts();
    
    // Initialize dropdown menus
    initDropdowns();
    
    // Initialize mobile sidebar toggle
    initMobileSidebar();
    
    // Initialize money formatting
    initMoneyFormatting();
    
    // Initialize file input labels
    initFileInputs();
});

/**
 * Initialize Bootstrap tooltips
 */
function initTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

/**
 * Initialize confirmation actions
 */
function initConfirmActions() {
    // Legacy delete buttons
    const deleteButtons = document.querySelectorAll('.btn-delete');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            if (!confirm('Вы уверены, что хотите удалить этот элемент?')) {
                e.preventDefault();
                return false;
            }
        });
    });
    
    // Data-confirm buttons
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
 * Initialize image preview functionality
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
 * Initialize auto-hiding alerts
 */
function initAutoHideAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.querySelector('.btn-close')) {
                alert.querySelector('.btn-close').click();
            } else {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        }, 5000);
    });
}

/**
 * Initialize dropdown menus
 */
function initDropdowns() {
    const dropdownToggle = document.querySelectorAll('.dropdown-toggle');
    dropdownToggle.forEach(dropdown => {
        dropdown.addEventListener('click', function(e) {
            e.preventDefault();
            const dropdownMenu = this.nextElementSibling;
            if (dropdownMenu.classList.contains('show')) {
                dropdownMenu.classList.remove('show');
            } else {
                // Close all open menus
                document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                    menu.classList.remove('show');
                });
                dropdownMenu.classList.add('show');
            }
        });
    });
    
    // Close menus when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.matches('.dropdown-toggle') && !e.target.closest('.dropdown-menu')) {
            document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
}

/**
 * Initialize mobile sidebar toggle
 */
function initMobileSidebar() {
    const sidebarToggle = document.querySelector('.navbar-toggler');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.querySelector('#sidebar').classList.toggle('show');
        });
    }
}

/**
 * Initialize money formatting
 */
function initMoneyFormatting() {
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
}

/**
 * Initialize file input labels
 */
function initFileInputs() {
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
}

/**
 * Function to dynamically add form fields
 * @param {string} containerId ID of container to add fields to
 * @param {string} templateId ID of field template
 */
function addFormField(containerId, templateId) {
    const container = document.getElementById(containerId);
    const template = document.getElementById(templateId);
    
    if (container && template) {
        const clone = template.content.cloneNode(true);
        const newFieldIndex = container.children.length;
        
        // Update indices in field names
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
 * Function to remove a form field
 * @param {Element} button Remove button
 */
function removeFormField(button) {
    const fieldGroup = button.closest('.form-group');
    if (fieldGroup) {
        fieldGroup.remove();
    }
} 