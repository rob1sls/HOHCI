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
import { PostReportDialogComponent } from '../post-report-dialog/post-report-dialog.component';

@Component({
  selector: 'update-dialog',
  templateUrl: './update-dialog.component.html',
  styleUrls: ['./update-dialog.component.css']
})
export class UpdateDialogComponent implements OnInit, OnDestroy {


  constructor(
    public dialogRef: MatDialogRef<PostReportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public dataPost: Post,
    private postService: PostService,
    private authService: AuthService, // Inject AuthService
    private userService: UserService,
    private matSnackbar: MatSnackBar
  ) { }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {
  }

  closeDialog() {
    this.dialogRef.close(); 
  }

}
