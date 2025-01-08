import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { AppConstants } from 'src/app/common/app-constants';
import { User } from 'src/app/model/user';
import { UserLogin } from 'src/app/model/user-login';
import { AuthService } from 'src/app/service/auth.service';
import { ForgotPasswordDialogComponent } from '../forgot-password-dialog/forgot-password-dialog.component';
import { SnackbarComponent } from '../snackbar/snackbar.component';
import { LoginResponseDto } from 'src/app/model/LoginResponseDto';
import { UserService } from 'src/app/service/user.service';
import { UpdateUserInfo } from 'src/app/model/update-user-info';



@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {
    loginFormGroup: FormGroup;
    submittingForm: boolean = false;

	group: string = '';


    private subscriptions: Subscription[] = [];

    constructor(
        private authService: AuthService,
        private userService: UserService,
		private route: ActivatedRoute,
        private router: Router,
        private formBuilder: FormBuilder,
        private matSnackbar: MatSnackBar,
        private matDialog: MatDialog) { }

    get email() { return this.loginFormGroup.get('email') }
    get password() { return this.loginFormGroup.get('password') }
	
	ngOnInit(): void {
	
		this.route.params.subscribe(params => {
			this.group = params['group']; // Récupérer la valeur du groupe (login1, login2, login3)
			this.group = this.group.slice(-1); // Récupérer le dernier caractère de la chaîne de caractères
			console.log('Groupe de connexion:', this.group);
		  });

		// Initialiser le formulaire avec FormBuilder
		this.loginFormGroup = this.formBuilder.group({
			username: ['', Validators.required], // Exemple d'input avec validation
			password: ['', Validators.required]  // Exemple d'input avec validation
		  });
	

	}

    ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }

    handleLogin(i:number): void {
        if (true) {
            this.submittingForm = true;
            const userLogin = new UserLogin();


            let emaildebut ="johndoe";
            let emailfin = "@gmail.com";
            userLogin.email = emaildebut + i + emailfin;
            userLogin.password = "Motdepasse!10";
			// on recup la derniere valeur de la chaine de caractere

			userLogin.email = userLogin.email + this.group;

			console.log(userLogin.email);
            this.subscriptions.push(
                this.authService.login(userLogin).subscribe({
                    next: (response: HttpResponse<LoginResponseDto>) => {
						console.log(response);

						  // Récupérer les informations dans la réponse
						  const authToken = response.headers.get('Jwt-Token');
						  const userId = response.body?.id;   // ID récupéré depuis la réponse
						  const studyGroup = response.body?.group; // Groupe récupéré depuis la réponse
						// Stocker le token dans le cache
						this.authService.storeTokenInCache(authToken);

						// Stocker l'utilisateur et autres informations
						this.authService.storeAuthUserInCache(response.body.user);
						// Vous pouvez maintenant utiliser userId et studyGroup comme nécessaire
						console.log('User ID:', userId);
						console.log('Study Group:', studyGroup);

                       
                        this.submittingForm = false;
						this.router.navigateByUrl('/profile');
                    },
                    error: (errorResponse: HttpErrorResponse) => {


                        this.handleLogin(i+1);
                        

                        

                    }
                })
            );
		}
        }

			
    






    openForgotPasswordDialog(e: Event): void {
        e.preventDefault();
        this.matDialog.open(ForgotPasswordDialogComponent, {
            autoFocus: true,
            width: '500px'
        });
    }
}
