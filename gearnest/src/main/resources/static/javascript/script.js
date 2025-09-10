

document.addEventListener('DOMContentLoaded', () => {
    // --- Elements ---
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');
    const userProfile = document.querySelector('.user-profile');
    const dropdownMenu = document.querySelector('.dropdown-menu');
    const locationFilter = document.getElementById('location-filter');
    const ratingFilter = document.getElementById('rating-filter');
    const resetFiltersBtn = document.getElementById('reset-filters-btn');
    const garageCards = document.querySelectorAll('.garage-card');
    const noResultsMessage = document.getElementById('no-results');
    const servicesFilterContainer = document.getElementById('services-filter-container');
    const vehicleTypeOptions = document.querySelectorAll('.vehicle-option');
    let serviceCheckboxes = [];

    // --- Mobile Menu Toggle ---
    mobileMenuButton.addEventListener('click', () => {
        mobileMenu.classList.toggle('is-open');
    });

    // --- User Profile Dropdown ---
    if (userProfile) {
        userProfile.addEventListener('click', (e) => {
            e.stopPropagation();
            userProfile.classList.toggle('active');
            dropdownMenu.classList.toggle('active');
        });
    }
    window.addEventListener('click', () => {
        if (dropdownMenu && dropdownMenu.classList.contains('active')) {
            userProfile.classList.remove('active');
            dropdownMenu.classList.remove('active');
        }
    });


    // --- Star Rating Generation ---
    function generateStars(rating) {
        const fullStar = `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'><path d='M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z' /></svg>`;
        const halfStar = `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'><path d='M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292zM10 12.956L12.247 14.4l-.585-2.51 1.823-1.66-2.52-.36L10 7.427v5.529z' /></svg>`;
        const emptyStar = `<svg style='color: var(--color-gray-300);' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'><path d='M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z' /></svg>`;
        let starsHTML = '';
        for (let i = 1; i <= 5; i++) {
            if (rating >= i) { starsHTML += fullStar; }
            else if (rating >= i - 0.5) { starsHTML += halfStar; }
            else { starsHTML += emptyStar; }
        }
        return `<div class='stars-container'>${starsHTML}</div><span class='rating-value'>${rating}</span>`;
    }

    document.querySelectorAll('[data-rating-display]').forEach(el => {
        const rating = parseFloat(el.dataset.ratingDisplay);
        el.innerHTML = generateStars(rating);
    });

    function initializeDynamicFilters() {
        const serviceMap = new Map();
        garageCards.forEach(card => {
            card.querySelectorAll('.service-tag').forEach(tag => {
                const value = tag.dataset.serviceValue;
                const displayName = tag.textContent;
                if (!serviceMap.has(value)) {
                    serviceMap.set(value, displayName);
                }
            });
        });

        const sortedServices = new Map([...serviceMap.entries()].sort((a, b) => a[1].localeCompare(b[1])));
        servicesFilterContainer.innerHTML = '';

        sortedServices.forEach((displayName, value) => {
            const checkboxHTML = `
                        <label class='service-label'>
                            <input type='checkbox' name='service' value='${value}'>
                            <span>${displayName}</span>
                        </label>`;
            servicesFilterContainer.insertAdjacentHTML('beforeend', checkboxHTML);
        });

        serviceCheckboxes = document.querySelectorAll('input[name="service"]');
        serviceCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', () => {
                checkbox.closest('.service-label').classList.toggle('selected', checkbox.checked);
                applyFilters();
            });
        });
    }

    function applyFilters() {
        const locationQuery = locationFilter.value.toLowerCase().trim();
        const minRating = parseFloat(ratingFilter.value);
        const selectedVehicle = document.querySelector('input[name="vehicle-type"]:checked').value;
        const selectedServices = Array.from(serviceCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value);
        let visibleCards = 0;

        garageCards.forEach(card => {
            const cardCity = card.dataset.city.toLowerCase();
            const cardRating = parseFloat(card.dataset.rating);
            const cardServices = card.dataset.services.split(' ');
            const cardVehicle = card.dataset.vehicle;

            const locationMatch = cardCity.includes(locationQuery);
            const ratingMatch = cardRating >= minRating;
            const vehicleMatch = selectedVehicle === 'all' || cardVehicle === selectedVehicle;
            const serviceMatch = selectedServices.length === 0 || selectedServices.every(service => cardServices.includes(service));

            if (locationMatch && ratingMatch && vehicleMatch && serviceMatch) {
                card.classList.remove('hidden-card');
                visibleCards++;
            } else {
                card.classList.add('hidden-card');
            }
        });

        noResultsMessage.style.display = visibleCards === 0 ? 'block' : 'none';
    }

    function resetFilters() {
        locationFilter.value = '';
        ratingFilter.value = '0';

        vehicleTypeOptions.forEach(opt => opt.classList.remove('selected'));
        const allVehicleOption = document.querySelector('input[name="vehicle-type"][value="all"]');
        allVehicleOption.checked = true;
        allVehicleOption.parentElement.classList.add('selected');

        serviceCheckboxes.forEach(checkbox => {
            checkbox.checked = false;
            checkbox.closest('.service-label').classList.remove('selected');
        });
        applyFilters();
    }

    // --- Modal Logic ---
    const modal = document.getElementById('details-modal');
    const modalContent = document.getElementById('modal-content');

    function showModal(card) {
        const title = card.querySelector('h3').textContent;
        const address = card.querySelector('.card-location span').textContent.trim();
        const rating = parseFloat(card.dataset.rating);
        const services = Array.from(card.querySelectorAll('.service-tag')).map(tag =>
            `<span class='service-tag' data-service-value='${tag.dataset.serviceValue}'>${tag.textContent}</span>`
        ).join('');

        modalContent.innerHTML = `
                    <div class='modal-body'>
                        <div class='modal-header'>
                            <h2 style='font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;'>${title}</h2>
                            <button id='close-modal-btn'>&times;</button>
                        </div>
                        <p style='color: var(--color-text-light); margin-bottom: 1rem;'>${address}</p>
                        <div class='rating-display' style='margin-bottom: 1rem;'>${generateStars(rating)}</div>
                        <h4 style='font-weight: 600; color: var(--color-gray-700); margin-bottom: 0.5rem;'>Services Offered:</h4>
                        <div class='service-tags' style='margin-bottom: 1rem;'>${services}</div>
                    </div>
                    <div class='modal-footer'>
                        <button class='btn btn-primary'>Contact Garage</button>
                    </div>`;

        modal.classList.add('is-visible');
        document.getElementById('close-modal-btn').addEventListener('click', hideModal);
    }

    function hideModal() {
        modal.classList.remove('is-visible');
    }

    document.querySelectorAll('.view-details-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const card = e.target.closest('.garage-card');
            showModal(card);
        });
    });

    modal.addEventListener('click', (e) => { if (e.target === modal) { hideModal(); } });
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && modal.classList.contains('is-visible')) { hideModal(); } });

    // --- Scroll Animation Observer ---
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) { entry.target.classList.add('is-visible'); }
        });
    }, { threshold: 0.1 });
    document.querySelectorAll('.scroll-animate').forEach(el => observer.observe(el));

    // --- Event Listeners ---
    locationFilter.addEventListener('keyup', applyFilters);
    ratingFilter.addEventListener('change', applyFilters);
    resetFiltersBtn.addEventListener('click', resetFilters);

    vehicleTypeOptions.forEach(option => {
        option.addEventListener('click', () => {
            vehicleTypeOptions.forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');
            option.querySelector('input').checked = true;
            applyFilters();
        });
    });

    // --- Initializations ---
    initializeDynamicFilters();
});



// DOM Elements
const hamburger = document.querySelector('.hamburger');
const navMenu = document.querySelector('.nav-menu');
const navLinks = document.querySelectorAll('.nav-link');
const navbar = document.querySelector('.navbar');
const sections = document.querySelectorAll('section');

// Toggle mobile menu
hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navMenu.classList.toggle('active');
});

// Close mobile menu when a nav link is clicked
navLinks.forEach(link => {
    link.addEventListener('click', () => {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');
        document.body.classList.remove('no-scroll');
    });
});

// Add scroll event for navbar transformation
window.addEventListener('scroll', () => {
    if (window.scrollY > 100) {
        navbar.classList.add('scrolled');
    } else {
        navbar.classList.remove('scrolled');
    }
});

// Highlight active section in navbar
window.addEventListener('scroll', () => {
    let current = '';

    sections.forEach(section => {
        const sectionTop = section.offsetTop;
        const sectionHeight = section.clientHeight;
        const scrollPosition = window.pageYOffset + navbar.offsetHeight;

        if (scrollPosition >= sectionTop && scrollPosition < (sectionTop + sectionHeight)) {
            current = section.getAttribute('id');
        }
    });

    navLinks.forEach(link => {
        link.classList.remove('active');
        if (link.getAttribute('href').substring(1) === current) {
            link.classList.add('active');
        }
    });
});

// Smooth scrolling for anchor links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();

        if (this.getAttribute('href') !== '#') {
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);

            if (targetElement) {
                const navbarHeight = navbar.offsetHeight;
                const targetPosition = targetElement.getBoundingClientRect().top + window.pageYOffset - navbarHeight;

                window.scrollTo({
                    top: targetPosition,
                    behavior: 'smooth'
                });
            }
        }
    });
});

// Add reveal animation to sections
const revealSections = () => {
    sections.forEach(section => {
        const sectionTop = section.getBoundingClientRect().top;
        const windowHeight = window.innerHeight;
        const revealPoint = 150;

        if (sectionTop < windowHeight - revealPoint) {
            section.classList.add('active');
        }
    });
};

window.addEventListener('scroll', revealSections);
// Initial call to reveal sections that might be in view on load
revealSections();



document.addEventListener('DOMContentLoaded', () => {
    // --- Mobile Menu Toggle ---
    const mobileMenuButton = document.getElementById('mobile-menu-button');
    const mobileMenu = document.getElementById('mobile-menu');
    mobileMenuButton.addEventListener('click', () => {
        mobileMenu.classList.toggle('hidden');
    });

    // --- Filter Elements & Logic ---
    const locationFilter = document.getElementById('location-filter');
    const ratingFilter = document.getElementById('rating-filter');
    const resetFiltersBtn = document.getElementById('reset-filters-btn');
    const garageCards = document.querySelectorAll('.garage-card');
    const noResultsMessage = document.getElementById('no-results');
    const servicesFilterContainer = document.getElementById('services-filter-container');
    let serviceCheckboxes = [];

    // --- Star Rating Generation ---
    function generateStars(rating) {
        const fullStar = '<svg class="w-5 h-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" /></svg>';
        const halfStar = '<svg class="w-5 h-5 text-yellow-400" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292zM10 12.956L12.247 14.4l-.585-2.51 1.823-1.66-2.52-.36L10 7.427v5.529z" /></svg>';
        const emptyStar = '<svg class="w-5 h-5 text-gray-300" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"><path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" /></svg>';
        let starsHTML = '';
        for (let i = 1; i <= 5; i++) {
            if (rating >= i) {
                starsHTML += fullStar;
            } else if (rating >= i - 0.5) {
                starsHTML += halfStar;
            } else {
                starsHTML += emptyStar;
            }
        }
        return `<div class="flex">${starsHTML}</div><span class="text-gray-700 ml-2 font-semibold">${rating}</span>`;
    }

    document.querySelectorAll('[data-rating-display]').forEach(el => {
        const rating = parseFloat(el.dataset.ratingDisplay);
        el.innerHTML = generateStars(rating);
    });


    function initializeDynamicFilters() {
        const serviceMap = new Map();
        garageCards.forEach(card => {
            const serviceTags = card.querySelectorAll('.service-tag');
            serviceTags.forEach(tag => {
                const value = tag.dataset.serviceValue;
                const displayName = tag.textContent;
                if (!serviceMap.has(value)) {
                    serviceMap.set(value, displayName);
                }
            });
        });

        const sortedServices = new Map([...serviceMap.entries()].sort((a, b) => a[1].localeCompare(b[1])));
        servicesFilterContainer.innerHTML = '';

        sortedServices.forEach((displayName, value) => {
            const checkboxHTML = `
                        <label class="service-label flex items-center space-x-2 text-sm text-gray-700 font-medium cursor-pointer whitespace-nowrap bg-gray-100 px-4 py-2 rounded-full border border-gray-200 hover:bg-gray-200 hover:border-gray-300 transition-colors">
                            <input type="checkbox" name="service" value="${value}" class="hidden">
                            <span>${displayName}</span>
                        </label>
                    `;
            servicesFilterContainer.insertAdjacentHTML('beforeend', checkboxHTML);
        });

        serviceCheckboxes = document.querySelectorAll('input[name="service"]');
        serviceCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', (event) => {
                const label = event.target.closest('.service-label');
                if (event.target.checked) {
                    label.classList.add('bg-blue-600', 'text-white', 'border-blue-600');
                    label.classList.remove('bg-gray-100', 'text-gray-700', 'border-gray-200');
                } else {
                    label.classList.remove('bg-blue-600', 'text-white', 'border-blue-600');
                    label.classList.add('bg-gray-100', 'text-gray-700', 'border-gray-200');
                }
                applyFilters();
            });
        });
    }

    function applyFilters() {
        const locationQuery = locationFilter.value.toLowerCase().trim();
        const minRating = parseFloat(ratingFilter.value);

        const selectedServices = Array.from(serviceCheckboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value);

        let visibleCards = 0;

        garageCards.forEach(card => {
            const cardCity = card.dataset.city.toLowerCase();
            const cardRating = parseFloat(card.dataset.rating);
            const cardServices = card.dataset.services.split(' ');

            const locationMatch = cardCity.includes(locationQuery);
            const ratingMatch = cardRating >= minRating;
            const serviceMatch = selectedServices.length === 0 || selectedServices.every(service => cardServices.includes(service));

            if (locationMatch && ratingMatch && serviceMatch) {
                card.classList.remove('hidden-card');
                visibleCards++;
            } else {
                card.classList.add('hidden-card');
            }
        });

        noResultsMessage.style.display = visibleCards === 0 ? 'block' : 'none';
    }

    function resetFilters() {
        locationFilter.value = '';
        ratingFilter.value = '0';
        serviceCheckboxes.forEach(checkbox => {
            checkbox.checked = false;
            const label = checkbox.closest('.service-label');
            label.classList.remove('bg-blue-600', 'text-white', 'border-blue-600');
            label.classList.add('bg-gray-100', 'text-gray-700', 'border-gray-200');
        });
        applyFilters();
    }

    // --- Modal Logic ---
    const modal = document.getElementById('details-modal');
    const modalContent = document.getElementById('modal-content');

    function showModal(card) {
        const title = card.querySelector('h3').textContent;
        const address = card.querySelector('p').textContent.trim();
        const rating = parseFloat(card.dataset.rating);
        const services = Array.from(card.querySelectorAll('.service-tag')).map(tag =>
            `<span class="${tag.className}">${tag.textContent}</span>`
        ).join('');

        modalContent.innerHTML = `
                    <div class="p-6">
                         <div class="flex justify-between items-start">
                             <h2 class="text-2xl font-bold text-gray-800 mb-2">${title}</h2>
                             <button id="close-modal-btn" class="text-gray-500 hover:text-gray-800 text-3xl leading-none">&times;</button>
                         </div>
                         <p class="text-gray-600 mb-4">${address}</p>
                         <div class="flex items-center mb-4">${generateStars(rating)}</div>
                         <h4 class="font-semibold text-gray-700 mb-2">Services Offered:</h4>
                         <div class="flex flex-wrap gap-2 mb-4">${services}</div>
                    </div>
                     <div class="bg-gray-50 px-6 py-4 text-right rounded-b-xl">
                        <button class="bg-blue-600 text-white font-semibold px-5 py-2 rounded-lg hover:bg-blue-700 transition">Contact Garage</button>
                    </div>
                `;

        modal.classList.remove('opacity-0', 'pointer-events-none');
        modalContent.classList.remove('scale-95', 'opacity-0');

        document.getElementById('close-modal-btn').addEventListener('click', hideModal);
    }

    function hideModal() {
        modal.classList.add('opacity-0', 'pointer-events-none');
        modalContent.classList.add('scale-95', 'opacity-0');
    }

    document.querySelectorAll('.view-details-btn').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const card = e.target.closest('.garage-card');
            showModal(card);
        });
    });

    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            hideModal();
        }
    });

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && !modal.classList.contains('pointer-events-none')) {
            hideModal();
        }
    });

    // --- Scroll Animation Observer ---
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('is-visible');
            }
        });
    }, {
        threshold: 0.1
    });

    document.querySelectorAll('.scroll-animate').forEach(el => {
        observer.observe(el);
    });


    // --- Event Listeners ---
    locationFilter.addEventListener('keyup', applyFilters);
    ratingFilter.addEventListener('change', applyFilters);
    resetFiltersBtn.addEventListener('click', resetFilters);

    // --- Initializations ---
    initializeDynamicFilters();
});



























// document.addEventListener('DOMContentLoaded', () => {

//     // --- Helper function to add event listeners safely ---
//     const addSafeEventListener = (element, event, handler) => {
//         if (element) {
//             element.addEventListener(event, handler);
//         }
//     };

//     // --- Elements ---
//     const mobileMenuButton = document.getElementById('mobile-menu-button');
//     const mobileMenu = document.getElementById('mobile-menu');
//     const userProfile = document.querySelector('.user-profile');
//     const dropdownMenu = document.querySelector('.dropdown-menu');
//     const locationFilter = document.getElementById('location-filter');
//     const ratingFilter = document.getElementById('rating-filter');
//     const resetFiltersBtn = document.getElementById('reset-filters-btn');
//     const garageCards = document.querySelectorAll('.garage-card');
//     const noResultsMessage = document.getElementById('no-results');
//     const servicesFilterContainer = document.getElementById('services-filter-container');
//     const vehicleTypeOptions = document.querySelectorAll('.vehicle-option');
//     const modal = document.getElementById('details-modal');
//     const hamburger = document.querySelector('.hamburger');
//     const navMenu = document.querySelector('.nav-menu');
//     const navLinks = document.querySelectorAll('.nav-link');
//     const navbar = document.querySelector('.navbar');
//     const sections = document.querySelectorAll('section');
//     let serviceCheckboxes = [];

//     // --- Mobile Menu Toggle ---
//     addSafeEventListener(mobileMenuButton, 'click', () => {
//         if (mobileMenu) mobileMenu.classList.toggle('is-open');
//     });

//     // --- Hamburger Menu Toggle ---
//     addSafeEventListener(hamburger, 'click', () => {
//         hamburger.classList.toggle('active');
//         if (navMenu) navMenu.classList.toggle('active');
//     });

//     // --- Close mobile menu when a nav link is clicked ---
//     navLinks.forEach(link => {
//         addSafeEventListener(link, 'click', () => {
//             if (hamburger) hamburger.classList.remove('active');
//             if (navMenu) navMenu.classList.remove('active');
//             document.body.classList.remove('no-scroll');
//         });
//     });

//     // --- User Profile Dropdown ---
//     addSafeEventListener(userProfile, 'click', (e) => {
//         e.stopPropagation();
//         if (dropdownMenu) {
//             userProfile.classList.toggle('active');
//             dropdownMenu.classList.toggle('active');
//         }
//     });

//     // --- Close dropdown on outside click ---
//     window.addEventListener('click', () => {
//         if (userProfile && dropdownMenu && dropdownMenu.classList.contains('active')) {
//             userProfile.classList.remove('active');
//             dropdownMenu.classList.remove('active');
//         }
//     });

//     // --- Star Rating Generation ---
//     function generateStars(rating) {
//         const fullStar = `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'><path d='M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z' /></svg>`;
//         const halfStar = `<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'><path d='M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292zM10 12.956L12.247 14.4l-.585-2.51 1.823-1.66-2.52-.36L10 7.427v5.529z' /></svg>`;
//         const emptyStar = `<svg style='color: var(--color-gray-300);' xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20' fill='currentColor'><path d='M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z' /></svg>`;
//         let starsHTML = '';
//         for (let i = 1; i <= 5; i++) {
//             if (rating >= i) { starsHTML += fullStar; }
//             else if (rating >= i - 0.5) { starsHTML += halfStar; }
//             else { starsHTML += emptyStar; }
//         }
//         return `<div class='stars-container'>${starsHTML}</div><span class='rating-value'>${rating.toFixed(1)}</span>`;
//     }

//     document.querySelectorAll('[data-rating-display]').forEach(el => {
//         const rating = parseFloat(el.dataset.ratingDisplay);
//         if (!isNaN(rating)) {
//             el.innerHTML = generateStars(rating);
//         }
//     });

//     // --- Garage Filtering Logic ---
//     // Only run if the necessary filter elements exist
//     if (locationFilter && ratingFilter && resetFiltersBtn && servicesFilterContainer) {

//         function applyFilters() {
//             // Function content remains the same
//         }
//         function resetFilters() {
//             // Function content remains the same
//         }
//         function initializeDynamicFilters() {
//             // Function content remains the same
//         }

//         // Setup filter event listeners
//         addSafeEventListener(locationFilter, 'keyup', applyFilters);
//         addSafeEventListener(ratingFilter, 'change', applyFilters);
//         addSafeEventListener(resetFiltersBtn, 'click', resetFilters);

//         vehicleTypeOptions.forEach(option => {
//             addSafeEventListener(option, 'click', () => {
//                 vehicleTypeOptions.forEach(opt => opt.classList.remove('selected'));
//                 option.classList.add('selected');
//                 const radio = option.querySelector('input');
//                 if (radio) radio.checked = true;
//                 applyFilters();
//             });
//         });

//         // Initialize the filters
//         initializeDynamicFilters();
//     }


//     // --- Modal Logic ---
//     if (modal) {
//         function hideModal() {
//             modal.classList.remove('is-visible');
//         }

//         function showModal(card) {
//             // function content to populate modal
//             // ...
//             modal.classList.add('is-visible');
//             const closeModalBtn = document.getElementById('close-modal-btn');
//             addSafeEventListener(closeModalBtn, 'click', hideModal);
//         }

//         document.querySelectorAll('.view-details-btn').forEach(btn => {
//             addSafeEventListener(btn, 'click', (e) => {
//                 const card = e.target.closest('.garage-card');
//                 if (card) showModal(card);
//             });
//         });

//         addSafeEventListener(modal, 'click', (e) => { if (e.target === modal) hideModal(); });
//         document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && modal.classList.contains('is-visible')) hideModal(); });
//     }


//     // --- Navbar Scroll Effects ---
//     window.addEventListener('scroll', () => {
//         if (navbar) {
//             if (window.scrollY > 100) {
//                 navbar.classList.add('scrolled');
//             } else {
//                 navbar.classList.remove('scrolled');
//             }
//         }
//     });

//     // --- General Scroll Animations & Effects ---
//     const observer = new IntersectionObserver((entries) => {
//         entries.forEach(entry => {
//             if (entry.isIntersecting) {
//                 entry.target.classList.add('is-visible');
//             }
//         });
//     }, { threshold: 0.1 });

//     document.querySelectorAll('.scroll-animate').forEach(el => observer.observe(el));
// });
