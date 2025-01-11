import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NavigationEnd, Router } from '@angular/router';
import { filter, Subscription } from 'rxjs';
import { AppConstants } from 'src/app/common/app-constants';
import { Notification } from 'src/app/model/notification';
import { User } from 'src/app/model/user';
import { AuthService } from 'src/app/service/auth.service';
import { NotificationService } from 'src/app/service/notification.service';
import { environment } from 'src/environments/environment';
import { PostDialogComponent } from '../post-dialog/post-dialog.component';
import { SearchDialogComponent } from '../search-dialog/search-dialog.component';
import { SnackbarComponent } from '../snackbar/snackbar.component';

@Component({
	selector: 'app-header',
	templateUrl: './header.component.html',
	styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
	authUser: User;
	isUserLoggedIn: boolean = false;
	isProfilePage: boolean = false;
	comingFromProfile: boolean = false; // Ajout pour surveiller la navigation depuis /profile

	notifications: Notification[] = [];
	hasUnseenNotification: boolean = false;
	resultPage: number = 1;
	resultSize: number = 5;
	hasMoreNotifications: boolean = false;
	fetchingResult: boolean = false;
	defaultProfilePhotoUrl = environment.defaultProfilePhotoUrl;
	showRewardMessage: boolean = false; // Ajouté pour afficher le message de récompense
	rewardMessageTimeout: any; // Pour gérer le délai d'affichage
	private reportExpListener: any; // Interval pour surveiller reportExp
	nevershowagain: boolean = false; // Pour ne pas afficher le message de récompense à nouveau

	
	private subscriptions: Subscription[] = [];

	constructor(
		private authService: AuthService,
		private notificationService: NotificationService,
		private matDialog: MatDialog,
		private router: Router, // Injection du Router

		private matSnackbar: MatSnackBar) { }

	ngOnInit(): void {
		if (this.authService.isUserLoggedIn()) {
			this.isUserLoggedIn = true;
			this.authUser = this.authService.getAuthUserFromCache();
		} else {
			this.isUserLoggedIn = false;
		}

		if (this.isUserLoggedIn) {
			this.loadNotifications(1);

		
		}

		this.subscriptions.push(
			this.router.events
				.pipe(filter(event => event instanceof NavigationEnd))
				.subscribe((event: NavigationEnd) => {
					this.comingFromProfile = this.isProfilePage; // Stocke l'état précédent
					this.isProfilePage = event.url === '/profile'; // Met à jour l'état actuel
				})
		);

		this.startReportExpListener();

		this.authService.logoutSubject.subscribe(loggedOut => {
			if (loggedOut) {
				this.isUserLoggedIn = false;
			}
		});

		this.authService.loginSubject.subscribe(loggedInUser => {
			if (loggedInUser) {
				this.authUser = loggedInUser;
				this.isUserLoggedIn = true;
			}
		});
	}

	ngOnDestroy(): void {
		this.subscriptions.forEach(sub => sub.unsubscribe());
	}

	openPostDialog(): void {
		this.matDialog.open(PostDialogComponent, {
			data: null,
			autoFocus: false,
			minWidth: '500px',
			maxWidth: '700px'
		});
	}

	openSearchDialog(): void {
		this.matDialog.open(SearchDialogComponent, {
			autoFocus: true,
			width: '500px'
		});
	}

	loadNotifications(page: number): void {
		this.fetchingResult = true;

		this.subscriptions.push(
			this.notificationService.getNotifications(page,  this.resultSize).subscribe({
				next: (notifications: Notification[]) => {
					this.fetchingResult = false;

					notifications.forEach(n => {
						this.notifications.push(n);
						if (!n.isSeen) this.hasUnseenNotification = true;
					});

					if (notifications.length > 0) {
						this.hasMoreNotifications = true;
					} else {
						this.hasMoreNotifications = false;
					}

					this.resultPage++;
				},
				error: (errorResponse: HttpErrorResponse) => {
					this.matSnackbar.openFromComponent(SnackbarComponent, {
						data: AppConstants.snackbarErrorContent,
						panelClass: ['bg-danger'],
						duration: 5000
					});
					this.fetchingResult = false;
				}
			})
		);
	}

	handleUnseenNotifications(): void {
		if (this.hasUnseenNotification) {
			this.subscriptions.push(
				this.notificationService.markAllSeen().subscribe({
					next: (response: any) => {
						this.hasUnseenNotification = false;
					},
					error: (errorResponse: HttpErrorResponse) => {
						this.matSnackbar.openFromComponent(SnackbarComponent, {
							data: AppConstants.snackbarErrorContent,
							panelClass: ['bg-danger'],
							duration: 5000
						});
					}
				})
			);
		}
	}

	startReportExpListener(): void {
		if (!this.nevershowagain) {
		this.reportExpListener = setInterval(() => {
			if (this.authUser?.reportExp >= 10 && !this.showRewardMessage) {
				this.handleExperienceChange();
			}
		}, 500); // Vérifie toutes les 500 ms
	}}

	handleExperienceChange(): void {
		this.showRewardMessage = false;
		this.rewardMessageTimeout = setTimeout(() => {
			this.showRewardMessage = false;
		}, 5000); // Afficher pendant 2 secondes
		this.nevershowagain = true; // Indiquer que la récompense a été affichée
		this.stopReportExpListener(); // Arrêter de surveiller l'expérience
	}

	stopReportExpListener(): void {
		clearInterval(this.reportExpListener);
	}
}
