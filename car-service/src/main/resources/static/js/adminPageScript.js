document.addEventListener("DOMContentLoaded", function() {
    const addServiceLink = document.querySelector(".add-service-link");
    const allServicesLink = document.querySelector(".all-services-link");
    const addMechanicLink = document.querySelector(".add-mechanic-link");
    const allMechanicsLink = document.querySelector(".mechanics-link");
    const allAppointments = document.querySelector(".appointments-link");
    const Users = document.querySelector(".users-link");
    const blockedUsers = document.querySelector(".blockedUsers-link");



    const addServiceForm = document.querySelector("#containerForServices");
    const servicesTable = document.querySelector("#serviceTable");
    const addMechanicsForm = document.querySelector("#containerForMechanic");
    const mechanicsTable = document.querySelector("#mechanicTable");
    const appointmentsTable = document.querySelector("#appointmentsContainer");
    const usersTable = document.querySelector("#userTable");
    const blockedUsersTable = document.querySelector("#blockedUserTable");




   


    addServiceLink.addEventListener("click", function(event) {
        event.preventDefault();
        addServiceForm.style.display = "block";
        servicesTable.style.display = "none";
        addMechanicsForm.style.display = "none";
        mechanicsTable.style.display = "none";
        appointmentsTable.style.display = "none";
        usersTable.style.display ="none";
        blockedUsersTable.style.display ="none";

    });

    allServicesLink.addEventListener("click", function(event) {
        event.preventDefault();
        
        addServiceForm.style.display = "none";
        servicesTable.style.display = "block";
        addMechanicsForm.style.display = "none";
        mechanicsTable.style.display = "none";
        appointmentsTable.style.display = "none";
        usersTable.style.display ="none";
        blockedUsersTable.style.display ="none";
    });

    addMechanicLink.addEventListener("click", function(event) {
        event.preventDefault();
        
        addServiceForm.style.display = "none";
        servicesTable.style.display = "none";
        addMechanicsForm.style.display = "block";
        mechanicsTable.style.display = "none";
        appointmentsTable.style.display = "none";
        usersTable.style.display ="none";
        blockedUsersTable.style.display ="none";
  });

    allMechanicsLink.addEventListener("click", function(event) {
    event.preventDefault();
    
    addServiceForm.style.display = "none";
    servicesTable.style.display = "none";
    addMechanicsForm.style.display = "none";
    mechanicsTable.style.display = "block";
    appointmentsTable.style.display = "none";
    usersTable.style.display ="none";
    blockedUsersTable.style.display ="none";
});
allAppointments.addEventListener("click", function(event) {
    event.preventDefault();
    
    addServiceForm.style.display = "none";
    servicesTable.style.display = "none";
    addMechanicsForm.style.display = "none";
    mechanicsTable.style.display = "none";
    appointmentsTable.style.display = "block";
    usersTable.style.display ="none";
    blockedUsersTable.style.display ="none";

});
Users.addEventListener("click", function(event) {
    event.preventDefault();

    usersTable.style.display ="block";
    addServiceForm.style.display = "none";
    servicesTable.style.display = "none";
    addMechanicsForm.style.display = "none";
    mechanicsTable.style.display = "none";
    appointmentsTable.style.display = "none";
    blockedUsersTable.style.display ="none";
    

});
blockedUsers.addEventListener("click", function(event) {
    event.preventDefault();

    blockedUsersTable.style.display ="block";
    usersTable.style.display ="none";
    addServiceForm.style.display = "none";
    servicesTable.style.display = "none";
    addMechanicsForm.style.display = "none";
    mechanicsTable.style.display = "none";
    appointmentsTable.style.display = "none";
    
    

});
});

