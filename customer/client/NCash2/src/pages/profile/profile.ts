import { Component } from '@angular/core';
import { NavController } from 'ionic-angular';
import { User } from '../../interfaces/User';
import { ChangePasswordPage } from './changePassword';
import { DeactivateAccountPage } from '../deactivateAccount/deactivateAccount';

@Component({
    selector: 'page-profile',
    templateUrl: 'profile.html'
})

export class ProfilePage {
    loggedInUser: Object;
    user: User = {
        name: 'abc',
        contact: 8951115922,
        address: 'xyz, bangalore',
        mail: 'abc@gmail.com',
        password: 'swaroop'
    };

    constructor(public navCtrl: NavController) {
        this.loggedInUser=new Object();
    }

    ionViewDidLoad() {
       
    }

    saveSettings() {
        
    }

    changePassword()
    {
        this.navCtrl.push(ChangePasswordPage, {oldPwd: this.user.password});
    }

    deactivateAccount()
    {
    	this.navCtrl.push(DeactivateAccountPage);
    }

}
