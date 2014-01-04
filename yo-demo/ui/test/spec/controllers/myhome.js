'use strict';

describe('Controller: MyhomeCtrl', function () {

  // load the controller's module
  beforeEach(module('uiApp'));

  var MyhomeCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    MyhomeCtrl = $controller('MyhomeCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of awesomeThings to the scope', function () {
    expect(scope.awesomeThings.length).toBe(3);
  });
});
