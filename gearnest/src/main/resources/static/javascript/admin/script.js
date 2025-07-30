const allSideMenu = document.querySelectorAll('#sidebar .side-menu.top li a');
allSideMenu.forEach(item => {
    const li = item.parentElement;

    item.addEventListener('click', function () {
        allSideMenu.forEach(i => {
            i.parentElement.classList.remove('active');
        })
        li.classList.add('active');
    })
});

// TOGGLE SIDEBAR
const menuBar = document.querySelector('#content nav .bx.bx-menu');
const sidebar = document.getElementById('sidebar');

// Sidebar toggle işlemi
menuBar.addEventListener('click', function () {
    sidebar.classList.toggle('hide');
});

// Sayfa yüklendiğinde ve boyut değişimlerinde sidebar durumunu ayarlama
function adjustSidebar() {
    if (window.innerWidth <= 576) {
        sidebar.classList.add('hide');  // 576px ve altı için sidebar gizli
        sidebar.classList.remove('show');
    } else {
        sidebar.classList.remove('hide');  // 576px'den büyükse sidebar görünür
        sidebar.classList.add('show');
    }
}

// Sayfa yüklendiğinde ve pencere boyutu değiştiğinde sidebar durumunu ayarlama
window.addEventListener('load', adjustSidebar);
window.addEventListener('resize', adjustSidebar);

// Arama butonunu toggle etme
const searchButton = document.querySelector('#content nav form .form-input button');
const searchButtonIcon = document.querySelector('#content nav form .form-input button .bx');
const searchForm = document.querySelector('#content nav form');

searchButton.addEventListener('click', function (e) {
    if (window.innerWidth < 768) {
        e.preventDefault();
        searchForm.classList.toggle('show');
        if (searchForm.classList.contains('show')) {
            searchButtonIcon.classList.replace('bx-search', 'bx-x');
        } else {
            searchButtonIcon.classList.replace('bx-x', 'bx-search');
        }
    }
})

// Dark Mode Switch
const switchMode = document.getElementById('switch-mode');

// Check saved mode on page load
document.addEventListener('DOMContentLoaded', function () {
    if (localStorage.getItem('theme') === 'dark') {
        document.body.classList.add('dark');
        switchMode.checked = true;
    } else {
        document.body.classList.remove('dark');
        switchMode.checked = false;
    }

    // Optional: Redraw all initialized DataTables
    if ($.fn.DataTable.isDataTable('#garageTable')) {
        $('#garageTable').DataTable().draw(false);
    }
});

switchMode.addEventListener('change', function () {
    if (this.checked) {
        document.body.classList.add('dark');
        localStorage.setItem('theme', 'dark');
    } else {
        document.body.classList.remove('dark');
        localStorage.setItem('theme', 'light');
    }

    // Optional: Redraw all initialized DataTables
    if ($.fn.DataTable.isDataTable('#garageTable')) {
        $('#garageTable').DataTable().draw(false);
    }
});


// Notification Menu Toggle
document.querySelector('.notification').addEventListener('click', function () {
    document.querySelector('.notification-menu').classList.toggle('show');
    document.querySelector('.profile-menu').classList.remove('show'); // Close profile menu if open
});

// Profile Menu Toggle
document.querySelector('.profile').addEventListener('click', function () {
    document.querySelector('.profile-menu').classList.toggle('show');
    document.querySelector('.notification-menu').classList.remove('show'); // Close notification menu if open
});

// Close menus if clicked outside
window.addEventListener('click', function (e) {
    if (!e.target.closest('.notification') && !e.target.closest('.profile')) {
        document.querySelector('.notification-menu').classList.remove('show');
        document.querySelector('.profile-menu').classList.remove('show');
    }
});

// Menülerin açılıp kapanması için fonksiyon
function toggleMenu(menuId) {
    var menu = document.getElementById(menuId);
    var allMenus = document.querySelectorAll('.menu');

    // Diğer tüm menüleri kapat
    allMenus.forEach(function (m) {
        if (m !== menu) {
            m.style.display = 'none';
        }
    });

    // Tıklanan menü varsa aç, yoksa kapat
    if (menu.style.display === 'none' || menu.style.display === '') {
        menu.style.display = 'block';
    } else {
        menu.style.display = 'none';
    }
}

// Başlangıçta tüm menüleri kapalı tut
document.addEventListener("DOMContentLoaded", function () {
    var allMenus = document.querySelectorAll('.menu');
    allMenus.forEach(function (menu) {
        menu.style.display = 'none';
    });
});

$(document).ready(function () {

    // Initialize garageTable if it exists
    if ($('#garageTable').length && $('#garageTable tbody tr').length > 0) {
        $('#garageTable').DataTable({
            responsive: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            order: [[0, 'asc']],
            columnDefs: [
                { orderable: false, targets: [1, 7] } // Adjust targets based on your actual columns
            ]
        });
    }

    // Initialize feedbackTable if it exists and has data
    if ($('#feedbackTable').length && $('#feedbackTable tbody tr').length > 0) {
        $('#feedbackTable').DataTable({
            responsive: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            order: [[0, 'asc']],
            columnDefs: [
                { orderable: false, targets: [5] } // Disable sorting for actions column
            ]
        });
    }

    // Initialize contactUsTable if it exists and has data
    if ($('#contactUsTable').length && $('#contactUsTable tbody tr').length > 0) {
        $('#contactUsTable').DataTable({
            responsive: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            order: [[0, 'asc']],
            columnDefs: [
                { orderable: false, targets: [6] } // Disable sorting for actions column
            ]
        });
    }

    // Initialize usersTable if it exists and has data

    if ($('#usersTable').length && $('#usersTable tbody tr').length > 0) {
        $('#usersTable').DataTable({
            responsive: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            order: [[0, 'asc']]
            // No columnDefs needed since all columns are sortable
        });
    }

    // Initialize servicesTable if it exists and has data
    if ($('#servicesTable').length && $('#servicesTable tbody tr').length > 0) {
        if ($.fn.DataTable.isDataTable('#servicesTable')) {
            $('#servicesTable').DataTable().clear().destroy();
        }

        $('#servicesTable').DataTable({
            responsive: true,
            pageLength: 5,
            lengthMenu: [5, 10, 25, 50],
            order: [[0, 'asc']],
            columnDefs: [
                { orderable: false, targets: [2] } // Disable sorting for "Action" column
            ]
        });
    }


});


document.addEventListener("DOMContentLoaded", function () {

    //admin feedback page
    document.querySelectorAll(".btn-delete-feedback").forEach(function (button) {
        button.addEventListener("click", function () {
            const feedbackId = this.getAttribute("data-id");
            const form = document.getElementById("deleteForm_" + feedbackId);

            if (!form) {
                console.error("Delete form not found for ID:", feedbackId);
                return;
            }

            Swal.fire({
                title: 'Are you sure?',
                text: "You won't be able to revert this!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Yes, delete it!'
            }).then((result) => {
                if (result.isConfirmed) {
                    form.submit();
                }
            });
        });
    });

    // admin contact page
    document.querySelectorAll(".btn-delete-contact").forEach(function (button) {
        button.addEventListener("click", function () {
            const contactId = this.getAttribute("data-id");
            const form = document.getElementById("deleteContactForm_" + contactId);

            if (!form) {
                console.error("Delete form not found for ID:", contactId);
                return;
            }

            Swal.fire({
                title: 'Are you sure?',
                text: "This message will be permanently deleted!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Yes, delete it!'
            }).then((result) => {
                if (result.isConfirmed) {
                    form.submit();
                }
            });
        });
    });

    document.querySelectorAll(".btn-respond-contact").forEach(function (button) {
        button.addEventListener("click", function () {
            const contactId = this.getAttribute("data-id");
            const form = document.getElementById("respondForm_" + contactId);
            const messageInput = document.getElementById("messageInput_" + contactId);
            const message = messageInput.value.trim();

            if (message === "") {
                Swal.fire({
                    icon: 'warning',
                    title: 'Reply is empty!',
                    text: 'Please write a response before sending.',
                });
                return;
            }

            // Show loading spinner
            Swal.fire({
                title: 'Sending...',
                text: 'Please wait while we send your response.',
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });

            const formData = new FormData(form);

            fetch(form.getAttribute("action"), {
                method: "POST",
                body: formData
            })
                .then(response => {
                    if (!response.ok) throw new Error("Server error");
                    return response.text();
                })
                .then(() => {
                    Swal.fire({
                        icon: 'success',
                        title: 'Response Sent!',
                        text: 'The reply has been emailed and marked as responded.',
                        timer: 2000,
                        showConfirmButton: false
                    }).then(() => {
                        window.location.reload(); // reload to reflect updated status
                    });
                })
                .catch(() => {
                    Swal.fire({
                        icon: 'error',
                        title: 'Failed to send!',
                        text: 'Something went wrong. Please try again.',
                    });
                });
        });
    });
});



// Add services on admin page

document.addEventListener("DOMContentLoaded", function () {
    const nameInput = document.getElementById("serviceName");
    const errorDiv = document.getElementById("serviceNameError");
    const form = document.getElementById("addServiceForm");
    const submitBtn = document.getElementById("submitServiceBtn");

    let isNameValid = false;

    function validateServiceName(callback = null) {
        const name = nameInput.value.trim();
        const regex = /^[A-Za-z\s]{3,50}$/;

        if (name === "") {
            errorDiv.textContent = "Service name is required.";
            errorDiv.classList.remove("d-none");
            errorDiv.classList.add("d-block");
            isNameValid = false;
            if (callback) callback(false);
            return;
        }

        if (!regex.test(name)) {
            errorDiv.textContent = "Only letters and spaces allowed (3–50 characters).";
            errorDiv.classList.remove("d-none");
            errorDiv.classList.add("d-block");
            isNameValid = false;
            if (callback) callback(false);
            return;
        }

        fetch(`/admin/services/api/services/check-name?name=${encodeURIComponent(name)}`)
            .then(res => res.json())
            .then(data => {
                if (data.exists) {
                    errorDiv.textContent = "This service name already exists.";
                    errorDiv.classList.remove("d-none");
                    errorDiv.classList.add("d-block");
                    isNameValid = false;
                } else {
                    errorDiv.textContent = "";
                    errorDiv.classList.add("d-none");
                    isNameValid = true;
                }
                if (callback) callback(isNameValid);
            })
            .catch(() => {
                errorDiv.textContent = "Error checking name. Try again.";
                errorDiv.classList.remove("d-none");
                errorDiv.classList.add("d-block");
                isNameValid = false;
                if (callback) callback(false);
            });
    }

    // Real-time validation
    nameInput.addEventListener("keyup", () => {
        validateServiceName();
    });

    // Submit button validation
    form.addEventListener("submit", function (e) {
        e.preventDefault(); // prevent default form submit
        submitBtn.disabled = true;

        validateServiceName(function (valid) {
            if (valid) {
                Swal.fire({
                    title: 'Adding Service...',
                    didOpen: () => Swal.showLoading(),
                    allowOutsideClick: false
                });

                // Submit form manually after validation
                form.submit();
            } else {
                submitBtn.disabled = false;
            }
        });
    });

    // Clear form and errors when modal is closed
    const addServiceModal = document.getElementById('addServiceModal');
    addServiceModal.addEventListener('hidden.bs.modal', function () {
        // Clear input
        document.getElementById('serviceName').value = '';

        // Hide and clear error
        const errorDiv = document.getElementById('serviceNameError');
        errorDiv.textContent = '';
        errorDiv.classList.add('d-none');

        // Re-enable submit button
        document.getElementById('submitServiceBtn').disabled = false;
    });
});

//  end of services add on page


// delete service on admin page

$(document).ready(function () {
    $(document).on('click', '.btn-delete-service', function () {
        const serviceId = $(this).data('id');
        const serviceName = $(this).data('name');

        console.log(serviceName + "  " + serviceId);

        Swal.fire({
            title: `Delete "${serviceName}"?`,
            text: "This action cannot be undone!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Yes, delete it!"
        }).then((result) => {
            if (result.isConfirmed) {
                $.ajax({
                    url: `/admin/services/delete?id=${serviceId}`,
                    type: "DELETE",
                    success: function () {
                        Swal.fire({
                            position: "top-end",
                            icon: "success",
                            title: "Service deleted successfully!",
                            showConfirmButton: false,
                            timer: 2000
                        }).then(() => {
                            location.reload();
                        });
                    },
                    error: function () {
                        Swal.fire("Error", "Failed to delete service.", "error");
                    }
                });
            }
        });
    });
});



// update services on admin page


// Open modal and fill data
$('.btn-update-service').click(function () {
    const id = $(this).data('id');
    const name = $(this).data('name');
    $('#updateServiceId').val(id);
    $('#updateServiceName').val(name);
    $('#updateServiceError').addClass('d-none').text('');
    $('#updateServiceModal').modal('show');
});

// Keyup validation
$('#updateServiceName').on('keyup', function () {
    const name = $(this).val().trim();
    const id = $('#updateServiceId').val();
    const errorEl = $('#updateServiceError');
    const regex = /^[A-Za-z\s]{3,50}$/;

    if (name === '') {
        errorEl.text('Service name is required.').removeClass('d-none');
        return;
    }

    if (!regex.test(name)) {
        errorEl.text('Only letters and spaces allowed (3–50 characters).').removeClass('d-none');
        return;
    }

    // AJAX check for uniqueness
    $.get('/admin/services/api/services/check-name', { name }, function (res) {
        if (res.exists && name.toLowerCase() !== $('.btn-update-service[data-id="' + id + '"]').data('name').toLowerCase()) {
            errorEl.text('Service name already exists.').removeClass('d-none');
        } else {
            errorEl.addClass('d-none').text('');
        }
    });
});

// Submit form
$('#updateServiceForm').on('submit', function (e) {
    e.preventDefault();

    const name = $('#updateServiceName').val().trim();
    const errorEl = $('#updateServiceError');

    if (errorEl.text().length > 0 && !errorEl.hasClass('d-none')) {
        return; // Don't submit if there's an error
    }

    Swal.fire({
        title: 'Updating...',
        allowOutsideClick: false,
        didOpen: () => Swal.showLoading()
    });

    $.post('/admin/services/update', $(this).serialize(), function () {
        Swal.fire({
            icon: 'success',
            title: 'Service updated successfully!',
            position: 'top-end',
            timer: 2000,
            showConfirmButton: false
        }).then(() => window.location.reload());
    });
});

