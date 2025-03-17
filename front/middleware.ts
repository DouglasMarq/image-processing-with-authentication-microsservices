import {MiddlewareConfig, NextRequest, NextResponse} from "next/server";
import axios from "axios";
import {cookies} from "next/headers";

const publicRoutes = [
    { path: '/register', whenAuthenticated: 'redirect' },
    { path: '/', whenAuthenticated: 'redirect' }
] as const

const REDIRECT_WHEN_NOT_AUTHENTICATED_ROUTE = '/'

export async function middleware(request: NextRequest) {
    const path = request.nextUrl.pathname;
    const publicRoute = publicRoutes.find(route => route.path === path);
    const authToken = request.cookies.get("accessToken");

    if (!authToken && publicRoute) {
        return NextResponse.next();
    }

    if (!authToken && !publicRoute) {
        const redirectUrl = request.nextUrl.clone();

        redirectUrl.pathname = REDIRECT_WHEN_NOT_AUTHENTICATED_ROUTE;

        return NextResponse.redirect(redirectUrl);
    }

    if (authToken && publicRoute && publicRoute.whenAuthenticated === 'redirect') {
        const redirectUrl = request.nextUrl.clone();

        redirectUrl.pathname = '/dashboard';

        return NextResponse.redirect(redirectUrl);
    }

    if (authToken && !publicRoute) {
        const cookieStore = await cookies();

        const refreshToken = cookieStore.get("refreshToken")!.value

        const res = await axios.post(`http://192.168.49.2:30080/v1/auth/refresh?refreshToken=${refreshToken}`);

        cookieStore.set("accessToken", res.data.accessToken, {
            httpOnly: true,
            sameSite: "lax",
            secure: false,
        });

        cookieStore.set("refreshToken", res.data.refreshToken, {
            httpOnly: true,
            sameSite: "lax",
            secure: false,
        });

        return NextResponse.next();
    }

    return NextResponse.next()
}

export const config: MiddlewareConfig = {
    matcher: [
        /*
         * Taken from https://nextjs.org/docs/app/building-your-application/routing/middleware#matcher
         * Match all request paths except for the ones starting with:
         * - api (API routes)
         * - _next/static (static files)
         * - _next/image (image optimization files)
         * - favicon.ico, sitemap.xml, robots.txt (metadata files)
         */
        '/((?!api|_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)',
    ],
}
