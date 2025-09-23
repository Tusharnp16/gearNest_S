$(document).ready(function () {

    // --- SWEETALERT SUCCESS/ERROR HANDLING ---
    const successMessageSpan = $('#success-message');
    if (successMessageSpan.length && successMessageSpan.text()) {
        const message = successMessageSpan.text();
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'success',
            title: message,
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
            }
        });
    }

    // Check for error messages from the backend on page load
    const errorMessageDiv = $('.alert-danger');
    if (errorMessageDiv.length && errorMessageDiv.text().trim() !== '') {
        const message = errorMessageDiv.text().trim();
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: message,
            confirmButtonColor: '#dc3545',
        });
    }

    // --- SWEETALERT DELETE CONFIRMATION ---
    $('.service-delete-btn').on('click', function (e) {
        e.preventDefault();
        const serviceId = $(this).data('id');
        const serviceName = $(this).data('name');
        Swal.fire({
            title: 'Are you sure?',
            text: `You are about to delete the service: "${serviceName}". This cannot be undone.`,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = `/garage/services/delete/${serviceId}`;
            }
        });
    });

    // --- FORM VALIDATION FOR ADDING SERVICE ---
    $('#add-service-form').on('submit', function (e) {
        let isValid = true;

        const serviceSelect = $('#serviceSelect');
        const vehicleTypeSelect = $('#vehicleType');
        const feeInput = $('#fee');

        // Reset errors
        $('#serviceError').text('');
        $('#vehicleTypeError').text('');
        $('#feeError').text('');

        if (!serviceSelect.val()) {
            $('#serviceError').text('Please select a service.');
            isValid = false;
        }

        if (!vehicleTypeSelect.val()) {
            $('#vehicleTypeError').text('Please select a vehicle type.');
            isValid = false;
        }

        const feeValue = feeInput.val();
        if (!feeValue) {
            $('#feeError').text('Please enter a fee.');
            isValid = false;
        } else if (isNaN(feeValue) || parseFloat(feeValue) <= 0) {
            $('#feeError').text('Please enter a valid fee greater than 0.');
            isValid = false;
        }

        if (!isValid) e.preventDefault();
    });
});

async function toggleServiceStatus(checkbox, serviceId) {
    try {
        const response = await fetch(`/garage/services/toggle/${serviceId}`, {
            method: 'GET',
        });
        const text = await response.text();
        if (response.ok) {
            // A successful response from the backend should trigger a redirect
            window.location.href = `/garage/services`;
        } else {
            checkbox.checked = !checkbox.checked;
            Swal.fire({
                toast: true,
                position: 'top-end',
                icon: 'error',
                title: 'Failed to update status',
                text: text,
                showConfirmButton: false,
                timer: 3000,
                timerProgressBar: true,
            });
        }
    } catch (error) {
        checkbox.checked = !checkbox.checked;
        Swal.fire({
            toast: true,
            position: 'top-end',
            icon: 'error',
            title: 'Network error',
            text: 'Could not connect to the server.',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
        });
    }
}
