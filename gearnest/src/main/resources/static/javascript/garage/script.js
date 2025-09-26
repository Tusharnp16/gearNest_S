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

    // --- SCRIPT FOR POPULATING STATE AND CITY DROPDOWNS ---
    const stateSelect = $('#state');
    const citySelect = $('#city');
    let savedStateId = stateSelect.data('selected-state-id');
    let savedCityId = citySelect.data('selected-city-id');

    // Function to populate the city dropdown based on stateId
    function populateCities(stateId) {
        citySelect.empty().append('<option value="">Select City</option>');
        if (stateId) {
            $.ajax({
                url: `/api/location/cities/${stateId}`,
                method: "GET",
                success: function (cities) {
                    $.each(cities, function (i, city) {
                        citySelect.append(`<option value="${city.id}">${city.name}</option>`);
                    });
                    // Pre-select the saved city if it exists and matches the state
                    if (savedCityId) {
                        citySelect.val(savedCityId);
                    }
                }
            });
        }
    }

    // Initial AJAX call to populate states on page load
    $.ajax({
        url: "/api/location/states",
        method: "GET",
        success: function (states) {
            stateSelect.empty().append('<option value="">Select State</option>');
            $.each(states, function (i, state) {
                stateSelect.append(`<option value="${state.id}">${state.name}</option>`);
            });
            // Pre-select the saved state and trigger the city population
            if (savedStateId) {
                stateSelect.val(savedStateId);
                populateCities(savedStateId);
            }
        }
    });

    // Event listener for state change
    stateSelect.on("change", function () {
        const stateId = $(this).val();
        // The savedCityId should only be used once, so we reset it to prevent issues
        // with subsequent state changes.
        savedCityId = null;
        populateCities(stateId);
    });

    // --- FORM SUBMISSION FOR PROFILE DETAILS ---
    $('#profileForm').on('submit', function (e) {
        e.preventDefault(); // Prevent the default form submission

        // Collect data from the form
        const garageName = $('#garageName').val();
        const ownerName = $('#ownerName').val();
        const phno = $('#phno').val();
        const address = $('#address').val();
        const description = $('#description').val();
        const openHour = $('select[name="openHour"]').val();
        const openPeriod = $('select[name="openPeriod"]').val();
        const closeHour = $('select[name="closeHour"]').val();
        const closePeriod = $('select[name="closePeriod"]').val();
        const stateId = $('#state').val();
        const cityId = $('#city').val();

        // Construct the opening hours string
        const openingHours = `${openHour} ${openPeriod} To ${closeHour} ${closePeriod}`;

        // Create the data object to send to the server
        const formData = {
            garageName: garageName,
            ownerName: ownerName,
            phno: phno,
            address: address,
            description: description,
            openingHours: openingHours,
            state: stateId ? { id: stateId } : null,
            city: cityId ? { id: cityId } : null,
        };

        $.ajax({
            url: "/garage/profile/update-details-json",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: function (response) {
                Swal.fire({
                    toast: true,
                    position: 'top-end',
                    icon: 'success',
                    title: response, // The success message from the backend
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true,
                });
            },
            error: function (xhr, status, error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: xhr.responseText || 'Failed to update details.',
                    confirmButtonColor: '#dc3545',
                });
            }
        });
    });
});

// The following functions remain outside the document.ready block as they are global
async function toggleServiceStatus(checkbox, serviceId) {
    try {
        const response = await fetch(`/garage/services/toggle/${serviceId}`, {
            method: 'GET',
        });
        const text = await response.text();
        if (response.ok) {
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

document.addEventListener('DOMContentLoaded', () => {
    const viewDetailsButtons = document.querySelectorAll('.view-details-btn');
    viewDetailsButtons.forEach(button => {
        button.addEventListener('click', async (event) => {
            event.stopPropagation(); // Prevent row click event from firing
            const bookingId = event.target.dataset.bookingId;
            try {
                const response = await fetch(`/garage/booking/${bookingId}`);
                if (!response.ok) {
                    throw new Error('Failed to fetch booking details.');
                }
                const booking = await response.json();

                // Corrected to handle a single service
                // const serviceName = booking.serviceNames;
                const serviceName = booking.serviceNames.map(s => `<ol>${s}</ol>`).join("");


                const statusOptions = ['Booked', 'In Progress', 'Completed', 'Cancelled'];
                const statusSelectHtml = `
                    <select id="status-select" class="swal2-select">
                        ${statusOptions.map(option => `
                            <option value="${option}" ${booking.status === option ? 'selected' : ''}>${option}</option>
                        `).join('')}
                    </select>
                `;

                Swal.fire({
                    title: `Booking #${booking.id} Details`,
                    customClass: 'custom-swal',
                    html: `
                        <div style="text-align: left; font-family: 'Inter', sans-serif;">
                            <h3>Customer Information</h3>
                            <p><strong>Name:</strong> ${booking.user.firstName} ${booking.user.lastName}</p>
                            <p><strong>Email:</strong> ${booking.user.email}</p>
                            <p><strong>Phone:</strong> ${booking.user.phone}</p>
                            <hr>
                            <h3>Vehicle & Service Details</h3>
                            <p><strong>Vehicle:</strong> ${booking.vehicleMake} ${booking.vehicleModel} (${booking.vehicleNumber})</p>
                            <p><strong>Pickup Address:</strong> ${booking.pickupAddress}</p>
                            <p><strong>Service:</strong> ${serviceName}</p>
                            <p><strong>Date & Time:</strong> ${new Date(booking.bookingDate).toLocaleDateString()} at ${booking.timeSlot}</p>
                            <hr>
                            <p><strong>Total Fee:</strong> ₹${booking.totalFee.toFixed(2)}</p>
                            <p><strong>Current Status:</strong> <span class="status-badge ${booking.status.toLowerCase()}">${booking.status}</span></p>
                        </div>
                    `,
                    showCancelButton: true,
                    confirmButtonText: 'Change Status',
                    cancelButtonText: 'Close',
                    focusConfirm: false
                }).then((result) => {
                    if (result.isConfirmed) {
                        Swal.fire({
                            title: 'Update Status',
                            html: 'Select a new status:' + statusSelectHtml,
                            showCancelButton: true,
                            confirmButtonText: 'Save',
                            showLoaderOnConfirm: true,
                            preConfirm: async () => {
                                const newStatus = document.getElementById('status-select').value;
                                try {
                                    const updateResponse = await fetch('/garage/booking/update-status', {
                                        method: 'POST',
                                        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                                        body: new URLSearchParams({
                                            'bookingId': bookingId,
                                            'status': newStatus
                                        })
                                    });

                                    if (!updateResponse.ok) {
                                        const errorText = await updateResponse.text();
                                        throw new Error(errorText || 'Failed to update status.');
                                    }
                                    return await updateResponse.text();
                                } catch (error) {
                                    Swal.showValidationMessage(`Request failed: ${error}`);
                                }
                            },
                            allowOutsideClick: () => !Swal.isLoading()
                        }).then((updateResult) => {
                            if (updateResult.isConfirmed) {
                                Swal.fire({
                                    toast: true,
                                    position: 'top-end',
                                    icon: 'success',
                                    title: updateResult.value,
                                    showConfirmButton: false,
                                    timer: 2000,
                                    timerProgressBar: true,
                                }).then(() => {
                                    location.reload();
                                });
                            }
                        });
                    }
                });
            } catch (error) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Failed to load booking details.' + error
                });
            }
        });
    });
});

