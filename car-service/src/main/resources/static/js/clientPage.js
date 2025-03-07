const body = document.querySelector("body");
const sidebar = document.querySelector(".sidebar");
const sidebarOpen = document.querySelector("#sidebarOpen");
const sidebarClose = document.querySelector(".collapse_sidebar");
const sidebarExpand = document.querySelector(".expand_sidebar");
sidebarOpen.addEventListener("click", () => sidebar.classList.toggle("close"));

sidebarClose.addEventListener("click", () => {
    sidebar.classList.add("close", "hoverable");
});
sidebarExpand.addEventListener("click", () => {
    sidebar.classList.remove("close", "hoverable");
});

sidebar.addEventListener("mouseenter", () => {
    if (sidebar.classList.contains("hoverable")) {
        sidebar.classList.remove("close");
    }
});
sidebar.addEventListener("mouseleave", () => {
    if (sidebar.classList.contains("hoverable")) {
        sidebar.classList.add("close");
    }
});

if (window.innerWidth < 768) {
    sidebar.classList.add("close");
} else {
    sidebar.classList.remove("close");

}
document.addEventListener("DOMContentLoaded", function() {
    const dayWorkLink = document.querySelector(".day-work-link");
    const weekWorkLink = document.querySelector(".week-work-link");
    const servicesLink = document.querySelector(".services-link");
    const dayWorkTable = document.querySelector("#tableDayWork");
    const weekWorkTable = document.querySelector("#tableWeekWork");
    const servicesTable = document.querySelector("#tableEndedAppointmets");
    const addAppointmentLink = document.querySelector(".add-appointment-link");
    const addAppointment = document.querySelector(".container");
    const historyLink  = document.querySelector(".history-link");
    const historyContainer = document.querySelector(".Your_cars");

    historyLink.addEventListener("click", function(event){
        event.preventDefault();
        historyContainer.style.display = "block";
        addAppointment.style.display = "none";
    })

    addAppointmentLink.addEventListener("click", function(event){
        event.preventDefault();
        addAppointment.style.display = "block";
        historyContainer.style.display = "none";
    });


    dayWorkLink.addEventListener("click", function(event) {
        event.preventDefault();
        dayWorkTable.style.display = "block";
        weekWorkTable.style.display = "none";
        servicesTable.style.display = "none";
    });

    weekWorkLink.addEventListener("click", function(event) {
        event.preventDefault();
        
        weekWorkTable.style.display = "block";
        dayWorkTable.style.display = "none";
        servicesTable.style.display = "none";
    });

    servicesLink.addEventListener("click", function(event) {
      event.preventDefault();
      servicesTable.style.display = "block"; // Показва таблицата за свършена работа
      dayWorkTable.style.display = "none"; // Скрива таблицата за работа през деня
      weekWorkTable.style.display = "none"; // Скрива таблицата за работа през седмицата

      
  
      // Ако проблемът продължава, опитайте с console.log, за да проверите дали се извиква тази функция.
      console.log("Clicked on Services Link");
  });
});

