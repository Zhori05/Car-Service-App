document.addEventListener('DOMContentLoaded', function () {

    let currentTab = 0; // текущата стъпка
    showTab(currentTab); // показва текущата стъпка

    function showTab(n) {
        const tabs = document.querySelectorAll('.tab');

        if (tabs.length > 0 && n >= 0 && n < tabs.length) {
            tabs.forEach((tab, index) => {
                tab.style.display = index === n ? 'block' : 'none';
            });

            // Активира бутоните за навигация
            const prevBtn = document.getElementById('prevBtn');
            const nextBtn = document.getElementById('nextBtn');

            if (n === 0) {
                prevBtn.style.display = 'none';
            } else {
                prevBtn.style.display = 'inline';
            }

            if (n === tabs.length - 1) {
                nextBtn.innerHTML = 'Направи запитване';
                nextBtn.type = 'submit'; // Промяна на типа на бутона на "submit"
            } else {
                nextBtn.innerHTML = 'Напред';
                nextBtn.type = 'button'; // Ако не е последната стъпка, възстановете типа на бутона
            }

            // Маркира текущата стъпка като активна
            updateProgressBar(n);
        } else {
            console.error('Стъпката не е намерена или не е валидна.');
        }
    }

    function nextPrev(n) {
        const tabs = document.querySelectorAll('.tab');

        

        tabs[currentTab].style.display = 'none';
        currentTab = currentTab + n;

        if (currentTab >= 0 && currentTab < tabs.length) {
            showTab(currentTab);
        } else {
            console.error('Стъпката не е намерена.');
        }
    }

    function validateForm() {
        const tabs = document.querySelectorAll('.tab');
        const inputs = tabs[currentTab].querySelectorAll('input, select, textarea');

        let isValid = true;

        for (let i = 0; i < inputs.length; i++) {
            if (inputs[i].value == '' && inputs[i].hasAttribute('data-addcar')) {
                inputs[i].classList.add('error');
                isValid = false;
            } else {
                inputs[i].classList.remove('error');
            }
        }

        return isValid;
    }


    function updateProgressBar(n) {
        const steps = document.querySelectorAll('.step');

        for (let i = 0; i < steps.length; i++) {
            steps[i].className = 'step';
        }

        steps[n].className = 'step active';
    }

    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    if (prevBtn && nextBtn) {
        prevBtn.addEventListener('click', function () {
            nextPrev(-1);
        });

        nextBtn.addEventListener('click', function () {
            nextPrev(1);
        });
    }
});
