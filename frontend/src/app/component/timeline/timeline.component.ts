import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { noop, Subscription } from 'rxjs';
import { AppConstants } from 'src/app/common/app-constants';
import { PostResponse } from 'src/app/model/post-response';
import { Tag } from 'src/app/model/tag';
import { AuthService } from 'src/app/service/auth.service';
import { PostService } from 'src/app/service/post.service';
import { TimelineService } from 'src/app/service/timeline.service';
import { SnackbarComponent } from '../snackbar/snackbar.component';
import { MatCheckboxModule } from '@angular/material/checkbox';



import { MatDialog } from '@angular/material/dialog';
import { AfterViewInit, ViewChild, TemplateRef } from '@angular/core';
import { UpdateDialogComponent } from '../update-dialog/update-dialog.component';
import { Post } from 'src/app/model/post';

@Component({
    selector: 'app-timeline',
    templateUrl: './timeline.component.html',
    styleUrls: ['./timeline.component.css']
})
export class TimelineComponent implements OnInit, OnDestroy {
    timelinePostResponseList: PostResponse[] = [];
    timelineTagList: Tag[] = [];
    noPost: boolean = false;
    resultPage: number = 1;
    resultSize: number = 200;
    hasMoreResult: boolean = true;
    fetchingResult: boolean = false;
    isTaggedPostPage: boolean = false;
    targetTagName: string;
    loadingTimelinePostsInitially: boolean = true;
    loadingTimelineTagsInitially: boolean = true;

    isUpdateAvailable: boolean = true; // Cette variable peut être changée selon la logique de ton app


    private subscriptions: Subscription[] = [];

    userId: string;
  

    constructor(
        private authService: AuthService,
        private timelineService: TimelineService,
        private postService: PostService,
        private route: ActivatedRoute,

        private router: Router,
        private activatedRoute: ActivatedRoute,
        private matSnackbar: MatSnackBar,
        private dialog: MatDialog,
        


        
      ) {}

    ngOnInit(): void {
        if (!this.authService.isUserLoggedIn()) {
            this.router.navigateByUrl('/login');
        } else {
            if (this.router.url !== '/') {
                this.targetTagName = this.activatedRoute.snapshot.paramMap.get('tagName');
                this.isTaggedPostPage = true;
                this.loadTaggedPosts(this.targetTagName, 1);
            } else {
                this.loadTimelinePosts(1);
            }

            this.loadTimelineTags();
        }

        this.route.params.subscribe(params => {
            this.userId = params['userId'];
            console.log('User ID:', this.userId);
        });
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(sub => sub.unsubscribe());
    }




    // Scroll event handler for infinite scrolling
    onScroll(event: any): void {
        const element = event.target;
        const isBottom = element.scrollHeight === element.scrollTop + element.clientHeight;
    
        if (isBottom && !this.fetchingResult && this.hasMoreResult) {
            this.fetchingResult = true;
            if (this.isTaggedPostPage) {
                this.loadTaggedPosts(this.targetTagName, this.resultPage);
            } else {
                this.loadTimelinePosts(this.resultPage);
            }
        }
    }

    loadTimelinePosts(currentPage: number): void {
        if (!this.fetchingResult) {
            this.fetchingResult = true;
            this.subscriptions.push(
                this.timelineService.getTimelinePosts(currentPage, this.resultSize).subscribe({
                    next: (postResponseList: PostResponse[]) => {
                        if (postResponseList.length === 0 && currentPage === 1) this.noPost = true;
                        
                        this.timelinePostResponseList.push(...postResponseList);

                        console.log('Timeline Post Response List:', this.timelinePostResponseList);
                        // compter le nombre de post qui ont comme hatefultype NOT 
                        let count = 0;
                        for (let i = 0; i < this.timelinePostResponseList.length; i++) {
                            if (this.timelinePostResponseList[i].post.hatefulType != 'NOT') {
                                count++;
                            }

                        }
                        console.log('Nombre de post avec hatefulType différent de NOT:', count);


                        // tous les 20 posts, dans la liste on ajoute un post special avec juste un texte
                        for (let i = 0; i < this.timelinePostResponseList.length; i++) {
                            if (i % 20 == 19) {
                                this.timelinePostResponseList[i].post.content = 'Bitte merken oder kopieren Sie sich unbedingt das folgende Wort als Code für den Post-Fragebogen: Aufmerksamkeitsdefizit';
                                this.timelinePostResponseList[i].post.hatefulType = 'BONJOUR';
                                this.timelinePostResponseList[i].post.likeCount = 0;
                                this.timelinePostResponseList[i].post.shareCount = 0;

                              
                            }
                        }
                        


                        this.hasMoreResult = postResponseList.length === this.resultSize;
                        this.resultPage++;
                        this.fetchingResult = false;
                        this.loadingTimelinePostsInitially = false;
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
    }

    loadTaggedPosts(tagName: string, currentPage: number): void {
        if (!this.fetchingResult) {
            this.fetchingResult = true;
            this.subscriptions.push(
                this.postService.getPostsByTag(tagName, currentPage, this.resultSize).subscribe({
                    next: (postResponseList: PostResponse[]) => {
                        if (postResponseList.length === 0 && currentPage === 1) this.noPost = true;

                        this.timelinePostResponseList.push(...postResponseList);
                        this.hasMoreResult = postResponseList.length === this.resultSize;
                        this.resultPage++;
                        this.fetchingResult = false;
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
    }

    loadTimelineTags(): void {
        this.fetchingResult = true;
        this.subscriptions.push(
            this.timelineService.getTimelineTags().subscribe({
                next: (tagList: Tag[]) => {
                    this.timelineTagList = tagList;
                    this.loadingTimelineTagsInitially = false;
                    this.fetchingResult = false;
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


    openUpdateDialog() {
        const dialogRef = this.dialog.open(UpdateDialogComponent, {
          data: {
            message: 'We have a new update for the badge system! Check it out now.'
          }
        });
    
        dialogRef.afterClosed().subscribe(result => {
          if (result === 'close') {
            this.isUpdateAvailable = false; // On peut cacher la notification si l'utilisateur l'a vue
          }
        });
      }


   
}