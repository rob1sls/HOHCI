import { HttpErrorResponse } from '@angular/common/http';
import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { AppConstants } from 'src/app/common/app-constants';
import { Post } from 'src/app/model/post';
import { User } from 'src/app/model/user';
import { PostService } from 'src/app/service/post.service';
import { AuthService } from 'src/app/service/auth.service'; // Import AuthService
import { environment } from 'src/environments/environment';
import { SnackbarComponent } from '../snackbar/snackbar.component';
import { UserService } from 'src/app/service/user.service';
import { UpdateUserInfo } from 'src/app/model/update-user-info';

@Component({
  selector: 'app-post-report-dialog',
  templateUrl: './post-report-dialog.component.html',
  styleUrls: ['./post-report-dialog.component.css']
})
export class PostReportDialogComponent implements OnInit, OnDestroy {

  reportReasons: string[] = [
    'Sexism',
    'Racism',
    'Discrimination',
    'Hate speech',
    'Other'
  ];
  selectedReason: string;
  selectedReasons = [];
  authUser: User; // Define authUser
  likeList: User[] = [];
  resultPage: number = 1;
  resultSize: number = 5;
  hasMoreResult: boolean = false;
  fetchingResult: boolean = false;
  defaultProfilePhotoUrl = environment.defaultProfilePhotoUrl;

  private subscriptions: Subscription[] = [];

  constructor(
    public dialogRef: MatDialogRef<PostReportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public dataPost: Post,
    private postService: PostService,
    private authService: AuthService, // Inject AuthService
    private userService: UserService,
    private matSnackbar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.authUser = this.authService.getAuthUserFromCache(); // Initialize authUser
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  closeDialog() {
    this.dialogRef.close();
  }

  postReported() {
    var hatefulselectedReason: string;
    console.log(this.selectedReason);
    if (this.selectedReason[0] === 'Hate speech') {
      hatefulselectedReason = 'hatespeech';

    } else if (this.selectedReason[0] === 'Offensive language') {
      hatefulselectedReason = 'offensivelanguage';

    } 
    else if (this.selectedReason[0] === 'Racism') {
      hatefulselectedReason = 'racism';

    }
    else if (this.selectedReason[0] === 'Sexism') {
      hatefulselectedReason = 'sexism';

    }
    else if (this.selectedReason[0] === 'Discrimination') {
      hatefulselectedReason = 'discrimination';

    }
    else if (this.selectedReason[0] === 'Other') {
      hatefulselectedReason = 'other';

    }

    this.postService.reportedPost(this.dataPost.id, hatefulselectedReason).subscribe(
          (response: any) => {
          // Retrieve the current authenticated user
          const currentUser = this.authService.getAuthUserFromCache();
            console.log("exp gagné : " + response);
          // Increment the experience points by 1
           currentUser.reportExp += response;

          // Update the user information in the cache
          this.authService.storeAuthUserInCache(currentUser);

          const updatedUserInfo: UpdateUserInfo = {
            firstName: currentUser.firstName,
            lastName: currentUser.lastName,
            gender: currentUser.gender,
            intro: currentUser.intro,
            hometown: currentUser.hometown,
            currentCity: currentUser.currentCity,
            eduInstitution: currentUser.eduInstitution,
            workplace: currentUser.workplace,
            countryName: null, // Ensure this field is in the User model as well
            birthDate: currentUser.birthDate,
            reportExp: currentUser.reportExp,
            numberofReport: 0,
            isUsed: false,
            group: 0
          };

          // Optionally, call userService to save the update in the database
          this.userService.updateUserInfo(updatedUserInfo).subscribe({
              next: () => {
                  this.dialogRef.close(response);
              },
              error: (error: HttpErrorResponse) => {
                  this.matSnackbar.openFromComponent(SnackbarComponent, {
                      data: AppConstants.snackbarErrorContent,
                      panelClass: ['bg-danger'],
                      duration: 5000
                  });
              }
          });
      },
          (error: HttpErrorResponse) => {
              console.error('Error reporting post:', error);
          }
      );
  }


   
}
