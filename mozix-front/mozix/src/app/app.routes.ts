import { Routes } from '@angular/router';
import { MainpageComponent } from './_components/mainpage/mainpage.component';
import { HomeComponent } from './_components/home/home.component';
import { ToldiComponent } from './_components/toldi/toldi.component';
import { CategoryComponent } from './_components/category/category.component';
import { CategoryInsideComponent } from './_components/categoryinside/categoryinside.component';
import { BestratedComponent } from './_components/bestrated/bestrated.component';
import { BestratedinsideComponent } from './_components/bestratedinside/bestratedinside.component';
import { NewreleaseComponent } from './_components/newrelease/newrelease.component';
import { ProfilComponent } from './_components/profil/profil.component';
import { EditprofilComponent } from './_components/editprofil/editprofil.component';
import { SearchResultsComponent } from './_components/search-results/search-results.component';

export const routes: Routes = [
  {path: '', redirectTo:'/mainpage', pathMatch:'full'},
  {path: 'mainpage', component:MainpageComponent},
  {path: 'home', component:HomeComponent},
  {path: 'toldi', component:ToldiComponent},
  {path: 'category', component:CategoryComponent},
  {path: 'categoryinside', component:CategoryInsideComponent},
  {path: 'bestrated', component:BestratedComponent},
  {path: 'bestratedinside', component:BestratedinsideComponent},
  {path: 'newrelease', component:NewreleaseComponent},
  {path: 'profil', component:ProfilComponent},
  {path: 'editprofil', component:EditprofilComponent},
  { path: 'search-results', component: SearchResultsComponent }, // Hozzáadva a keresési eredmények útvonala
  {
    path: 'vígjáték',
    component: CategoryInsideComponent,
    data: { genre: 'Vígjáték' }
  },
  {
    path: 'dráma',
    component: CategoryInsideComponent,
    data: { genre: 'Dráma' }
  },
  {
    path: 'historical',
    component: CategoryInsideComponent,
    data: { genre: 'Történelmi' }
  },
  {
    path: 'Háborús', // Az URL szegmens
    component: CategoryInsideComponent,
    data: { genre: 'Háborús' } // Az átadott műfajnév
  },

  { path: 'best-rated', component: BestratedComponent }, // A fő "Legjobb értékelésű" oldal
  {
    path: 'best-rated/5',
    component: BestratedinsideComponent,
    data: { rating: 5 }
  },
  {
    path: 'best-rated/4',
    component: BestratedinsideComponent,
    data: { rating: 4 }
  },
  {
    path: 'best-rated/3',
    component: BestratedinsideComponent,
    data: { rating: 3 }
  },
  {
    path: 'best-rated/2',
    component: BestratedinsideComponent,
    data: { rating: 2 }
  },
  {
    path: 'best-rated/1',
    component: BestratedinsideComponent,
    data: { rating: 1 }
  }
];