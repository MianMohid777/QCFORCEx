describe('Category Test', () => {
    it('verifies fail alert', () => {
      cy.visit('http://localhost:8080/');
      cy.get('.btn-secondary').click();
      cy.get('#username').type('admin');
      cy.get('#password').type('aaa');
      cy.get('.btn').click();
      cy.get(':nth-child(1) > .card > .card-body > .card-link').click();
      cy.get('[style="margin: 20px 0"]').click();
  
  
  
      cy.on('window:alert', (alertMessage) => {
        expect(alertMessage).to.contain('Failed to add Category'); 
      });
     
      cy.get('#name').wait(1000).type('Test Category');
      cy.get('form > .modal-footer > .btn-primary').click();
  
      
    });
  });
  