document.addEventListener('DOMContentLoaded', function () {
document.getElementById('service').addEventListener('change', function () {
    // Извличане на избраната услуга и нейният обхват
    var selectedService = this.options[this.selectedIndex];
    var serviceRange = selectedService.getAttribute('data-range');

    // Филтриране на механиците в селект менюто в зависимост от обхвата на услугата
    var mechanicSelect = document.getElementById('mechanic');
    for (var i = 0; i < mechanicSelect.options.length; i++) {
        var mechanic = mechanicSelect.options[i];
        var specializedIn = mechanic.getAttribute('data-specializedin');

        if (specializedIn === serviceRange || serviceRange === 'All') {
            mechanic.disabled = false;
        } else {
            mechanic.disabled = true;
        }
    }
});
});